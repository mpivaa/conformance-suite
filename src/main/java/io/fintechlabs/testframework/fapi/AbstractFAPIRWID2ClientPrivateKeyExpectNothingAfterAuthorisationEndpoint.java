package io.fintechlabs.testframework.fapi;

import io.fintechlabs.testframework.condition.as.AddPrivateKeyJWTToServerConfiguration;


public abstract class AbstractFAPIRWID2ClientPrivateKeyExpectNothingAfterAuthorisationEndpoint extends AbstractFAPIRWID2ClientTest {

	@Override
	protected void addTokenEndpointAuthMethodSupported() {

		callAndStopOnFailure(AddPrivateKeyJWTToServerConfiguration.class);
	}

	@Override
	protected Object authorizationEndpoint(String requestId){

		Object returnValue = super.authorizationEndpoint(requestId);

		getTestExecutionManager().runInBackground(() -> {
			Thread.sleep(5 * 1000);
			if (getStatus().equals(Status.WAITING)) {
				setStatus(Status.RUNNING);
				//As the client hasn't called the token endpoint after 5 seconds, assume it has correctly detected the error and aborted.
				fireTestFinished();
			}

			return "done";

		});

		return returnValue;
	}

}
