package io.fintechlabs.testframework.fapiciba;

import io.fintechlabs.testframework.condition.client.RemoveIssFromRequestObject;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;

@PublishTestModule(
	testName = "fapi-ciba-poll-with-mtls-ensure-request-object-missing-iss-fails",
	displayName = "FAPI-CIBA: Poll mode - missing 'iss' value in request object, should return an error (MTLS client authentication)",
	summary = "This test should return an error that the 'iss' value in request object from back channel authentication endpoint request is missing",
	profile = "FAPI-CIBA",
	configurationFields = {
		"server.discoveryUrl",
		"client.client_id",
		"client.scope",
		"client.jwks",
		"client.hint_type",
		"client.hint_value",
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
public class FAPICIBAPollWithMTLSEnsureRequestObjectMissingIssFails extends AbstractFAPICIBAWithMTLSEnsureRequestObjectFails {
	@Variant(name = FAPICIBA.variant_poll_mtls)
	public void setupMTLS() {
		// FIXME: add private key variant
	}

	@Override
	protected void cleanupAfterBackchannelRequestShouldHaveFailed() {
		pollCleanupAfterBackchannelRequestShouldHaveFailed();
	}

	@Override
	protected void createAuthorizationRequestObject() {
		super.createAuthorizationRequestObject();
		callAndStopOnFailure(RemoveIssFromRequestObject.class, "CIBA-7.1.1");
	}

	@Override
	protected void modeSpecificAuthorizationEndpointRequest() {
		/* Nothing to do */
	}

}
