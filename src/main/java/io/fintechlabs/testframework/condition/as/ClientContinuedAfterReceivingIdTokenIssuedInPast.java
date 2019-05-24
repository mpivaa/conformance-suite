package io.fintechlabs.testframework.condition.as;

import io.fintechlabs.testframework.condition.AbstractCondition;
import io.fintechlabs.testframework.testmodule.Environment;

public class ClientContinuedAfterReceivingIdTokenIssuedInPast extends AbstractCondition {

	@Override
	public Environment evaluate(Environment env) {

		logFailure("Client has incorrectly called token_endpoint after following a HTTP 301 Redirect from the ciba_notification_endpoint.");

		return env;
	}

}
