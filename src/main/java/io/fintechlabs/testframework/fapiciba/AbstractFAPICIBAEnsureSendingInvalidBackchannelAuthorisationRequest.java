package io.fintechlabs.testframework.fapiciba;

import io.fintechlabs.testframework.condition.Condition;
import io.fintechlabs.testframework.condition.client.CheckErrorFromBackchannelAuthenticationEndpointErrorInvalidRequest;

// Send invalid request to backchannel authorisation endpoint and the response is invalid_request
public abstract class AbstractFAPICIBAEnsureSendingInvalidBackchannelAuthorisationRequest extends AbstractFAPICIBA {

	@Override
	protected void performAuthorizationFlow() {
		performPreAuthorizationSteps();

		eventLog.startBlock(currentClientString() + "Call backchannel authentication endpoint");

		createAuthorizationRequest();

		performAuthorizationRequest();

		eventLog.endBlock();

		validateErrorFromBackchannelAuthorizationRequestResponse();

		callAndContinueOnFailure(CheckErrorFromBackchannelAuthenticationEndpointErrorInvalidRequest.class, Condition.ConditionResult.FAILURE, "CIBA-13");

		cleanupAfterBackchannelRequestShouldHaveFailed();
	}

	@Override
	protected void waitForAuthenticationToComplete(long delaySeconds) {
		//Not called in this test
	}

}
