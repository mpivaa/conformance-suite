package net.openid.conformance.openbanking_brasil.testmodules.support;

import net.openid.conformance.condition.client.AbstractJsonAssertingCondition;
import net.openid.conformance.testmodule.Environment;

public class PaymentsProxyCheckForInvalidStatus extends AbstractJsonAssertingCondition {

	@Override
	public Environment evaluate(Environment env) {

		boolean checkStatus = env.getBoolean("payment_proxy_check_for_reject");

		if (checkStatus) {
			boolean consent_rejected = env.getBoolean("consent_rejected");
			if (!consent_rejected) {
				throw error("Payment status in an invalid state, exiting");
			}
		}
		return env;
	}
}
