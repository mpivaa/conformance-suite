package net.openid.conformance.condition.as;

import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.runner.TestDispatcher;
import net.openid.conformance.testmodule.Environment;

public class FAPIBrazilGenerateServerConfiguration extends AbstractCondition {

	@Override
	@PreEnvironment(strings = "base_url")
	@PostEnvironment(required = "server", strings = { "issuer", "discoveryUrl" })
	public Environment evaluate(Environment env) {

		String baseUrl = env.getString("base_url");

		if (baseUrl.isEmpty()) {
			throw error("Base URL is empty");
		}

		// set off the URLs below with a slash, if needed
		if (!baseUrl.endsWith("/")) {
			baseUrl = baseUrl + "/";
		}

		String baseUrlMtls = baseUrl.replaceFirst(TestDispatcher.TEST_PATH, TestDispatcher.TEST_MTLS_PATH);

		// create a base server configuration object based on the base URL
		JsonObject server = new JsonObject();
		JsonObject mtlsAliases = new JsonObject();

		server.addProperty("issuer", baseUrl);
		server.addProperty("authorization_endpoint", baseUrl + "authorize");

		server.addProperty("token_endpoint", baseUrl + "token");
		mtlsAliases.addProperty("token_endpoint", baseUrlMtls + "token");

		server.addProperty("jwks_uri", baseUrl + "jwks");

		server.addProperty("registration_endpoint", baseUrl + "register");
		mtlsAliases.addProperty("registration_endpoint", baseUrlMtls + "register");

		server.addProperty("userinfo_endpoint", baseUrl + "userinfo");
		mtlsAliases.addProperty("userinfo_endpoint", baseUrlMtls + "userinfo");

		server.add("mtls_endpoint_aliases", mtlsAliases);

		server.addProperty("request_parameter_supported", true);
		// add this as the server configuration
		env.putObject("server", server);

		env.putString("issuer", baseUrl);
		env.putString("discoveryUrl", baseUrl + ".well-known/openid-configuration");

		logSuccess("Created server configuration", args("server", server, "issuer", baseUrl, "discoveryUrl", baseUrl + ".well-known/openid-configuration"));

		return env;

	}

}
