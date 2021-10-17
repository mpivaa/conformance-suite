package net.openid.conformance.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;

public class CallClientConfigurationEndpoint extends AbstractCondition {

	@Override
	@PreEnvironment(required = "client")
	@PostEnvironment(required = "registration_client_endpoint_response")
	public Environment evaluate(Environment env) {

		String accessToken = env.getString("registration_access_token");
		if (Strings.isNullOrEmpty(accessToken)){
			throw error("Couldn't find registration_access_token.");
		}

		String registrationClientUri = env.getString("client", "registration_client_uri");
		if (Strings.isNullOrEmpty(registrationClientUri)){
			throw error("Couldn't find registration_client_uri.");
		}

		try {

			RestTemplate restTemplate = createRestTemplate(env);
			restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
				@Override
				public boolean hasError(ClientHttpResponse response) throws IOException {
					// Treat all http status codes as 'not an error', so spring never throws an exception due to the http
					// status code meaning the rest of our code can handle http status codes how it likes
					return false;
				}
			});
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			headers.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
			headers.set("Authorization", String.join(" ", "Bearer", accessToken));

			HttpEntity<?> request = new HttpEntity<>(headers);
			try {
				ResponseEntity<String> response = restTemplate.exchange(registrationClientUri, HttpMethod.GET, request, String.class);
				JsonObject responseInfo = convertJsonResponseForEnvironment("registration_client_uri", response);

				env.putObject("registration_client_endpoint_response", responseInfo);

				logSuccess("Called registration_client_uri", responseInfo);

			} catch (RestClientResponseException e) {
				throw error("Error from registration_client_uri", args("code", e.getRawStatusCode(), "status", e.getStatusText(), "body", e.getResponseBodyAsString()));
			} catch (RestClientException e) {
				return handleClientException(env, registrationClientUri, e);
			}

		} catch (NoSuchAlgorithmException | KeyManagementException | CertificateException | InvalidKeySpecException | KeyStoreException | IOException | UnrecoverableKeyException e) {
			throw error("Error creating HTTP Client", e);
		}

		return env;
	}

	protected Environment handleClientException(Environment env, String registrationClientUri, RestClientException e) {
		String msg = "Call to registration_client_uri " + registrationClientUri + " failed";
		if (e.getCause() != null) {
			msg += " - " + e.getCause().getMessage();
		}
		throw error(msg, e);
	}
}
