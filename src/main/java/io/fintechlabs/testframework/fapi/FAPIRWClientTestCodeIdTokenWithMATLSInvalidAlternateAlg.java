package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.condition.as.ForceIdTokenToBeSignedWithRS256;
import io.fintechlabs.testframework.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-rw-client-test-code-id-token-with-matls-invalid-alternate-alg",
	displayName = "FAPI-RW: client test - if the alg of id_token is PS256, then sign with RS256 in the authorization endpoint, should be rejected (code id_token with MTLS)",
	summary = "This test should end with the client displaying an error message that the algorithm used to sign the id_token does not match the required algorithm",
	profile = "FAPI-RW",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	}
)

public class FAPIRWClientTestCodeIdTokenWithMATLSInvalidAlternateAlg extends AbstractFAPIRWClientExpectNothingAfterAuthorisationEndpoint {

	@Override
	protected void addCustomValuesToIdToken() {

		//Do Nothing
	}

	@Override
	protected void addCustomSignatureOfIdToken(){

		callAndStopOnFailure(ForceIdTokenToBeSignedWithRS256.class,"OIDCC-3.1.3.7-8");
	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		throw new ConditionError(getId(), "Client has incorrectly called token_endpoint after receiving an id_token with an alg value of RS256 (it was originally PS256) from the authorization_endpoint.");
	}

}
