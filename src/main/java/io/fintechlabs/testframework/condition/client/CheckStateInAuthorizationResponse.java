package io.fintechlabs.testframework.condition.client;

import com.google.common.base.Strings;
import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.condition.PreEnvironment;
import io.fintechlabs.testframework.testmodule.Environment;

public class CheckStateInAuthorizationResponse extends AbstractCondition {

	@Override
	@PreEnvironment(required = "authorization_endpoint_response")
	public Environment evaluate(Environment env) {

		String actual = env.getString("authorization_endpoint_response", "state");
		String expected = env.getString("state");

		if (Strings.isNullOrEmpty(actual)) {
			// we didn't save a 'state' value, we need to make sure one wasn't returned
			if (Strings.isNullOrEmpty(actual)) {
				// we're good
				logSuccess("No state in response to check");
				return env;
			} else {
				throw error("No state value was sent, but a state in response was returned", args("expected", Strings.nullToEmpty(expected), "actual", Strings.nullToEmpty(actual)));
			}
		} else {
			// we did save a state parameter, make sure it's the same as before
			if (expected.equals(actual)) {
				// we're good
				logSuccess("State in response correctly returned", args("state", actual));

				return env;
			} else {
				throw error("State in response did not match",  args("expected", Strings.nullToEmpty(expected), "actual", Strings.nullToEmpty(actual)));
			}
		}
	}
}
