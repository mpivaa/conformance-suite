package io.fintechlabs.testframework.openbanking;

import io.fintechlabs.testframework.condition.as.AddTLSClientAuthToServerConfiguration;
import io.fintechlabs.testframework.condition.as.RemoveAtHashFromIdToken;
import io.fintechlabs.testframework.fapi.FAPIRWID2ClientTest;
import io.fintechlabs.testframework.testmodule.PublishTestModule;
import io.fintechlabs.testframework.testmodule.Variant;

@PublishTestModule(
	testName = "fapi-rw-id2-ob-client-test-with-mtls-holder-of-key-missing-athash",
	displayName = "FAPI-RW-ID2-OB: client test - id_token without an at_hash value from the authorization_endpoint, should be rejected (with MTLS)",
	profile = "FAPI-RW-ID2-OB",
	configurationFields = {
		"server.jwks",
		"client.client_id",
		"client.scope",
		"client.redirect_uri",
		"client.certificate",
		"client.jwks",
	},
	notApplicableForVariants = {
		FAPIRWID2ClientTest.variant_mtls,
		FAPIRWID2ClientTest.variant_privatekeyjwt,
		FAPIRWID2ClientTest.variant_openbankinguk_privatekeyjwt
	}
)

public class FAPIRWID2OBClientTestWithMTLSHolderOfKeyNoAtHash extends AbstractFAPIRWID2OBClientTest {

	@Variant(name = variant_openbankinguk_mtls)
	public void setupOpenBankingUkMTLS() {
		super.setupOpenBankingUkMTLS();
	}

	@Override
	protected void addTokenEndpointAuthMethodSupported() {

		callAndContinueOnFailure(AddTLSClientAuthToServerConfiguration.class);
	}

	@Override
	protected void addCustomValuesToIdToken() {

		callAndStopOnFailure(RemoveAtHashFromIdToken.class, "OIDCC-3.3.2.9");
	}

}
