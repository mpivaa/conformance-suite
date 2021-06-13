package net.openid.conformance.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class GetDynamicClientConfiguration extends AbstractCondition {

	@Override
	@PreEnvironment(required = "config")
	@PostEnvironment(required = "dynamic_client_registration_template")
	public Environment evaluate(Environment env) {
		JsonElement dynamicClientRegistrationTemplate = env.getElementFromObject("config", "client");
		if (dynamicClientRegistrationTemplate == null || !dynamicClientRegistrationTemplate.isJsonObject()) {
			// we don't actually need anything; if no client_name is given we'll pick one
			env.putObject("dynamic_client_registration_template", new JsonObject());
			log("No client details on configuration, created an empty dynamic_client_registration_template object.");
		} else {
			// we've got a client object, put it in the environment
			env.putObject("dynamic_client_registration_template", dynamicClientRegistrationTemplate.getAsJsonObject());

			log("Created dynamic_client_registration_template object from the client configuration.", dynamicClientRegistrationTemplate.getAsJsonObject());
		}
		return env;
	}
}
