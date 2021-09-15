package net.openid.conformance.condition.client.ekyc;

import com.google.gson.JsonElement;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;

public class ValidateEvidenceSupportedInServerConfiguration extends AbstractCondition {

	@Override
	@PreEnvironment(required = "server")
	public Environment evaluate(Environment env) {
		JsonElement jsonElement = env.getElementFromObject("server", "evidence_supported");
		if(jsonElement == null) {
			throw error("evidence_supported is not set");
		}
		if(!jsonElement.isJsonArray()) {
			throw error("evidence_supported must be a json array", args("actual", jsonElement));
		}
		//TODO require at least one entry? or is an empty value is also allowed?

		logSuccess("evidence_supported is valid", args("actual", jsonElement));
		return env;
	}
}
