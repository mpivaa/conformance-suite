package io.fintechlabs.testframework.openbanking;

import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.condition.as.SignIdTokenInvalid;
import io.fintechlabs.testframework.testmodule.PublishTestModule;

@PublishTestModule(
	testName = "fapi-ob-client-test-code-id-token-with-private-key-jwt-and-matls-invalid-signature",
	displayName = "FAPI-OB: client test - invalid signature in id_token from authorization_endpoint should be rejected (code id_token with private_key_jwt and MATLS)",
	summary = "This test should end with the client displaying an error message that the signature in the id_token from the authorization_endpoint does not match the signature value in the request object",
	profile = "FAPI-OB",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	}
)

public class FAPIOBClientTestCodeIdTokenWithPrivateKeyJWTAndMATLSInvalidSignature extends AbstractFAPIOBClientPrivateKeyExpectNothingAfterAuthorisationEndpoint {

	@Override
	protected void addCustomValuesToIdToken() {
		//Do Nothing
	}

	@Override
	protected void addCustomSignatureOfIdToken(){

		callAndStopOnFailure(SignIdTokenInvalid.class, "OIDCC-3.1.3.7-6");

	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		throw new ConditionError(getId(), "Client has incorrectly called token_endpoint after receiving an id_token with an invalid signature from the authorization_endpoint.");

	}

}
