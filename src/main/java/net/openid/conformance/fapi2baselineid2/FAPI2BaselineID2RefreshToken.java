package net.openid.conformance.fapi2baselineid2;

import com.google.common.base.Strings;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.AddPromptConsentToAuthorizationEndpointRequestIfScopeContainsOfflineAccess;
import net.openid.conformance.condition.client.CDRRefreshTokenRequiredWhenSharingDurationRequested;
import net.openid.conformance.condition.client.EnsureRefreshTokenContainsAllowedCharactersOnly;
import net.openid.conformance.condition.client.EnsureServerConfigurationSupportsRefreshToken;
import net.openid.conformance.condition.client.ExtractIdTokenFromTokenResponse;
import net.openid.conformance.condition.client.ExtractRefreshTokenFromTokenResponse;
import net.openid.conformance.condition.client.FAPIBrazilRefreshTokenRequired;
import net.openid.conformance.condition.client.FAPIEnsureServerConfigurationDoesNotSupportRefreshToken;
import net.openid.conformance.condition.client.ValidateRefreshTokenNotRotated;
import net.openid.conformance.sequence.ConditionSequence;
import net.openid.conformance.sequence.client.RefreshTokenRequestExpectingErrorSteps;
import net.openid.conformance.sequence.client.RefreshTokenRequestSteps;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.variant.FAPI1FinalOPProfile;
import net.openid.conformance.variant.FAPI2ID2OPProfile;

@PublishTestModule(
	testName = "fapi2-baseline-id2-refresh-token",
	displayName = "FAPI2-Baseline-ID2: test refresh token behaviours",
	summary = "This tests obtains refresh tokens and performs various checks, including checking that the refresh token is correctly bound to the client.",
	profile = "FAPI2-Baseline-ID2",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client.jwks",
		"mtls.key",
		"mtls.cert",
		"mtls.ca",
		"client2.client_id",
		"client2.scope",
		"client2.jwks",
		"mtls2.key",
		"mtls2.cert",
		"mtls2.ca",
		"resource.resourceUrl"
	}
)
public class FAPI2BaselineID2RefreshToken extends AbstractFAPI2BaselineID2MultipleClient {

	protected void addPromptConsentToAuthorizationEndpointRequest() {
		callAndStopOnFailure(AddPromptConsentToAuthorizationEndpointRequestIfScopeContainsOfflineAccess.class, "OIDCC-11");
	}

	@Override
	protected void createAuthorizationRequest() {
		super.createAuthorizationRequest();
		addPromptConsentToAuthorizationEndpointRequest();
	}

	protected void sendRefreshTokenRequestAndCheckIdTokenClaims() {
		eventLog.startBlock(currentClientString() + "Check for refresh token");
		callAndContinueOnFailure(ExtractRefreshTokenFromTokenResponse.class, Condition.ConditionResult.INFO);

		//stop if no refresh token is returned
		if(Strings.isNullOrEmpty(env.getString("refresh_token"))) {
			if (getVariant(FAPI2ID2OPProfile.class) == FAPI2ID2OPProfile.CONSUMERDATARIGHT_AU) {
				// this will always fail & stop
				callAndStopOnFailure(CDRRefreshTokenRequiredWhenSharingDurationRequested.class, "CDR-requesting-sharing-duration");
			}
			if (getVariant(FAPI2ID2OPProfile.class) == FAPI2ID2OPProfile.OPENBANKING_BRAZIL) {
				// this will always fail & stop
				callAndStopOnFailure(FAPIBrazilRefreshTokenRequired.class, "BrazilOB-5.2.2-12");
			}
			callAndContinueOnFailure(FAPIEnsureServerConfigurationDoesNotSupportRefreshToken.class, Condition.ConditionResult.WARNING, "OIDCD-3");
			// This throws an exception: the test will stop here
			fireTestSkipped("Refresh tokens cannot be tested. No refresh token was issued.");
		}
		callAndContinueOnFailure(EnsureServerConfigurationSupportsRefreshToken.class, Condition.ConditionResult.WARNING, "OIDCD-3");
		callAndContinueOnFailure(EnsureRefreshTokenContainsAllowedCharactersOnly.class, Condition.ConditionResult.FAILURE, "RFC6749-A.17");
		eventLog.endBlock();
		ConditionSequence sequence = new RefreshTokenRequestSteps(isSecondClient(), addTokenEndpointClientAuthentication, isDpop);
		if (getVariant(FAPI2ID2OPProfile.class) == FAPI2ID2OPProfile.OPENBANKING_BRAZIL) {
			sequence = sequence.insertAfter(ExtractIdTokenFromTokenResponse.class,
				condition(ValidateRefreshTokenNotRotated.class).requirement("BrazilOB-5.2.2-17").dontStopOnFailure());
		}
		call(sequence);
	}

	@Override
	protected void onPostAuthorizationFlowComplete() {
		if (!isSecondClient()) {
			// Try the second client

			//remove refresh token from 1st client
			env.removeNativeValue("refresh_token");

			// Remove token mappings
			// (This must be done before restarting the authorization flow, because
			// handleSuccessfulAuthorizationEndpointResponse extracts an id token)
			env.unmapKey("access_token");
			env.unmapKey("id_token");

			performAuthorizationFlowWithSecondClient();
		} else {
			switchToClient1AndTryClient2AccessToken();

			// try client 2's refresh_token with client 1
			eventLog.startBlock("Attempting to use refresh_token issued to client 2 with client 1");
			call(new RefreshTokenRequestExpectingErrorSteps(isSecondClient(), addTokenEndpointClientAuthentication, isDpop));
			eventLog.endBlock();
			fireTestFinished();
		}
	}

	@Override
	protected void exchangeAuthorizationCode() {
		// Store the original access token and ID token separately (see RefreshTokenRequestSteps)
		env.mapKey("access_token", "first_access_token");
		env.mapKey("id_token", "first_id_token");

		super.exchangeAuthorizationCode();

		// Set up the mappings for the refreshed access and ID tokens
		env.mapKey("access_token", "second_access_token");
		env.mapKey("id_token", "second_id_token");

		sendRefreshTokenRequestAndCheckIdTokenClaims();
	}
}
