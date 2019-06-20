package io.fintechlabs.testframework.fapiciba;

import io.fintechlabs.testframework.condition.client.AddClientIdToBackchannelAuthenticationEndpointRequest;
import io.fintechlabs.testframework.condition.client.AddRequestObjectClaimsToBackchannelAuthenticationEndpointRequest;
import io.fintechlabs.testframework.condition.client.CallBackchannelAuthenticationEndpoint;
import io.fintechlabs.testframework.condition.client.CreateBackchannelAuthenticationEndpointRequest;

public abstract class AbstractFAPICIBAEnsureBackchannelAuthorizationRequestWithoutRequestFailsWithMTLS extends AbstractFAPICIBAEnsureSendingInvalidBackchannelAuthorisationRequest {

	protected void performAuthorizationRequest() {

		callAndStopOnFailure(CreateBackchannelAuthenticationEndpointRequest.class, "CIBA-7.1");

		callAndStopOnFailure(AddClientIdToBackchannelAuthenticationEndpointRequest.class);

		callAndStopOnFailure(AddRequestObjectClaimsToBackchannelAuthenticationEndpointRequest.class, "CIBA-7.1");

		callAndStopOnFailure(CallBackchannelAuthenticationEndpoint.class);
	}

}
