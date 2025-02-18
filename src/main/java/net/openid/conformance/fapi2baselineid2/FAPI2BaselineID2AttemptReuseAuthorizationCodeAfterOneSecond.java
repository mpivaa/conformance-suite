package net.openid.conformance.fapi2baselineid2;

import net.openid.conformance.condition.Condition;
import net.openid.conformance.condition.client.CallProtectedResource;
import net.openid.conformance.condition.client.CallTokenEndpointAndReturnFullResponse;
import net.openid.conformance.condition.client.CheckErrorDescriptionFromTokenEndpointResponseErrorContainsCRLFTAB;
import net.openid.conformance.condition.client.CheckErrorFromTokenEndpointResponseErrorInvalidGrant;
import net.openid.conformance.condition.client.CheckTokenEndpointHttpStatus400;
import net.openid.conformance.condition.client.CheckTokenEndpointReturnedJsonContentType;
import net.openid.conformance.condition.client.EnsureHttpStatusCodeIs4xx;
import net.openid.conformance.condition.client.ServerAllowedReusingAuthorizationCode;
import net.openid.conformance.condition.client.ValidateErrorDescriptionFromTokenEndpointResponseError;
import net.openid.conformance.condition.client.ValidateErrorFromTokenEndpointResponseError;
import net.openid.conformance.condition.client.ValidateErrorUriFromTokenEndpointResponseError;
import net.openid.conformance.condition.client.WaitForOneSecond;
import net.openid.conformance.sequence.ConditionSequence;
import net.openid.conformance.sequence.client.CreateJWTClientAuthenticationAssertionAndAddToTokenEndpointRequest;
import net.openid.conformance.testmodule.PublishTestModule;
import net.openid.conformance.variant.ClientAuthType;
import net.openid.conformance.variant.VariantSetup;
import org.apache.http.HttpStatus;

@PublishTestModule(
	testName = "fapi2-baseline-id2-attempt-reuse-authorisation-code-after-one-second",
	displayName = "FAPI2-Baseline-ID2: try to reuse authorization code after one second",
	summary = "This test tries reusing an authorization code after one second, as the authorization code has already been used this must fail with the AS returning an invalid_grant error.",
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
public class FAPI2BaselineID2AttemptReuseAuthorizationCodeAfterOneSecond extends AbstractFAPI2BaselineID2ServerTestModule {

	private Class<? extends ConditionSequence> generateNewClientAssertionSteps;

	protected void waitForAmountOfTime() {
		callAndStopOnFailure(WaitForOneSecond.class);
	}

	protected void verifyError() {
		Integer httpStatus = env.getInteger("token_endpoint_response_http_status");
		if (httpStatus == HttpStatus.SC_OK) {
			callAndContinueOnFailure(ServerAllowedReusingAuthorizationCode.class, Condition.ConditionResult.FAILURE, "FAPI1-BASE-5.2.2-13");
		} else {
			callAndContinueOnFailure(CheckTokenEndpointHttpStatus400.class, Condition.ConditionResult.FAILURE, "OIDCC-3.1.3.4");
			callAndContinueOnFailure(CheckTokenEndpointReturnedJsonContentType.class, Condition.ConditionResult.FAILURE, "OIDCC-3.1.3.4");
			callAndContinueOnFailure(CheckErrorFromTokenEndpointResponseErrorInvalidGrant.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");
			callAndContinueOnFailure(ValidateErrorFromTokenEndpointResponseError.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");
			callAndContinueOnFailure(CheckErrorDescriptionFromTokenEndpointResponseErrorContainsCRLFTAB.class, Condition.ConditionResult.WARNING, "RFC6749-5.2");
			callAndContinueOnFailure(ValidateErrorDescriptionFromTokenEndpointResponseError.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");
			callAndContinueOnFailure(ValidateErrorUriFromTokenEndpointResponseError.class, Condition.ConditionResult.FAILURE, "RFC6749-5.2");
		}
	}

	@Override
	protected void onPostAuthorizationFlowComplete() {

		eventLog.startBlock("Attempting reuse of authorisation code");

		waitForAmountOfTime();

		// We're testing that reuse of the _code_ is refused. Reusing the client assertion
		// (only present for private_key_jwt) is also an error, so generate a new one here.
		if (generateNewClientAssertionSteps != null) {
			call(sequence(generateNewClientAssertionSteps));
		}

		if (isDpop) {
			createDpopForTokenEndpoint(false);
		}
		callAndContinueOnFailure(CallTokenEndpointAndReturnFullResponse.class, Condition.ConditionResult.WARNING, "FAPI1-BASE-5.2.2-13");

		verifyError();

		eventLog.startBlock("Testing if access token was revoked after authorization code reuse (the AS 'should' have revoked the access token)");
		updateResourceRequest();
		callAndStopOnFailure(CallProtectedResource.class, Condition.ConditionResult.FAILURE, "RFC6749-4.1.2");
		call(exec().mapKey("endpoint_response", "resource_endpoint_response_full"));

		callAndContinueOnFailure(EnsureHttpStatusCodeIs4xx.class, Condition.ConditionResult.WARNING, "RFC6749-4.1.2", "RFC6750-3.1");

		call(exec().unmapKey("endpoint_response"));

		eventLog.endBlock();

		fireTestFinished();
	}

	@VariantSetup(parameter = ClientAuthType.class, value = "mtls")
	@Override
	public void setupMTLS() {
		super.setupMTLS();
		generateNewClientAssertionSteps = null;
	}

	@VariantSetup(parameter = ClientAuthType.class, value = "private_key_jwt")
	@Override
	public void setupPrivateKeyJwt() {
		super.setupPrivateKeyJwt();
		generateNewClientAssertionSteps = CreateJWTClientAuthenticationAssertionAndAddToTokenEndpointRequest.class;
	}
}
