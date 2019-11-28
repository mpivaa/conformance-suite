package net.openid.conformance.condition.client;

import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.testmodule.Environment;

public class ExpectCallbackWithInvalidMissingNonceError extends AbstractCondition {

	@Override
	public Environment evaluate(Environment env) {
		createBrowserInteractionPlaceholder("The client must show an error message that the nonce in the id_token from the authorization_endpoint is missing - upload a log file or screenshot of the error.");
		return env;
	}

}
