package io.fintechlabs.testframework.openbanking;

import io.fintechlabs.testframework.condition.Condition.ConditionResult;
import io.fintechlabs.testframework.condition.ConditionError;
import io.fintechlabs.testframework.condition.as.AddInvalidExpiredIatValueToIdToken;
import io.fintechlabs.testframework.condition.as.ClientContinuedAfterReceivingIdTokenIssuedInPast;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@PublishTestModule(
	testName = "fapi-rw-id2-ob-client-test-with-private-key-jwt-and-mtls-holder-of-key-invalid-expired-iat",
	displayName = "FAPI-RW-ID2-OB: client test - expired iat value in id_token from authorization_endpoint, should be rejected (with private_key_jwt and MTLS)",
	summary = "This test should end with the client displaying an error message that the iat value in the id_token (from the authorization_endpoint) has expired (in the request object)",
	profile = "FAPI-RW-ID2-OB",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	}
)

public class FAPIRWID2OBClientTestWithPrivateKeyJWTAndMTLSHolderOfKeyInvalidExpiredIat extends AbstractFAPIRWID2OBClientPrivateKeyExpectNothingAfterAuthorisationEndpoint {

	@Override
	protected void addCustomValuesToIdToken() {

		callAndStopOnFailure(AddInvalidExpiredIatValueToIdToken.class, "OIDCC-3.1.3.7-10");
	}

	@Override
	protected Object authorizationCodeGrantType(String requestId) {

		callAndContinueOnFailure(ClientContinuedAfterReceivingIdTokenIssuedInPast.class, ConditionResult.WARNING);
		setStatus(Status.WAITING);
		fireTestFinished();
		return new ResponseEntity<Object>("Client has incorrectly called token_endpoint after receiving an id_token with an expired iat value from the authorization_endpoint.", HttpStatus.NOT_ACCEPTABLE);
	}

}
