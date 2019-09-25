package io.fintechlabs.testframework.condition.client;

import io.fintechlabs.testframework.condition.PostEnvironment;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.testmodule.Environment;

public class ExtractAccessTokenFromTokenResponse extends AbstractExtractAccessToken {

	@Override
	@PreEnvironment(required = "token_endpoint_response")
	@PostEnvironment(required = "access_token")
	public Environment evaluate(Environment env) {

		return extractAccessToken(env, "token_endpoint_response");
	}

}
