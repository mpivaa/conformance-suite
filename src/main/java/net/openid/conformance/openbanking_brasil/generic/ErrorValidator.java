package net.openid.conformance.openbanking_brasil.generic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.condition.client.AbstractJsonAssertingCondition;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.testmodule.OIDFJSON;
import net.openid.conformance.util.field.ArrayField;
import net.openid.conformance.util.field.Field;
import net.openid.conformance.util.field.StringField;

import java.util.function.Consumer;

public class ErrorValidator extends AbstractJsonAssertingCondition {

	@Override
	@PreEnvironment(strings = "resource_endpoint_response")
	public Environment evaluate(Environment environment) {
		JsonObject body = bodyFrom(environment);
		Integer status = environment.getInteger("resource_endpoint_response_status");

		assertHasField(body, "$.errors");
		assertOuterFields(body);
		assertInnerFields(body, status);

		return environment;
	}

	private void assertOuterFields(JsonObject body) {
		JsonObject errors = findByPath(body, "$").getAsJsonObject();
		assertField(errors, new ArrayField
			.Builder("errors")
			.setMinItems(1)
			.setMaxItems(13)
			.build()
		);
	}
	private void assertInnerFields(JsonObject body, Integer status) {
		JsonArray errors = findByPath(body, "$.errors").getAsJsonArray();
		errors.forEach(error -> {
			assertField(error.getAsJsonObject(),
				new StringField
					.Builder("code")
					.setPattern("^" + status + "$")
					.build());
		});
	}
}
