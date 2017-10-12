/*******************************************************************************
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package io.fintechlabs.testframework.example;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.gson.JsonObject;

import io.fintechlabs.testframework.condition.CheckServerConfiguration;
import io.fintechlabs.testframework.condition.GetStaticClientConfiguration;
import io.fintechlabs.testframework.condition.GetStaticServerConfiguration;
import io.fintechlabs.testframework.frontChannel.BrowserControl;
import io.fintechlabs.testframework.logging.EventLog;
import io.fintechlabs.testframework.testmodule.AbstractTestModule;
import io.fintechlabs.testframework.testmodule.TestModule.Status;

/**
 * @author jricher
 *
 */
public class SampleClientTestModule extends AbstractTestModule {

	/**
	 * @param name
	 */
	public SampleClientTestModule() {
		super("sample-client-test");
		this.status = Status.CREATED;
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#configure(com.google.gson.JsonObject, io.fintechlabs.testframework.logging.EventLog, java.lang.String, io.fintechlabs.testframework.frontChannel.BrowserControl, java.lang.String)
	 */
	@Override
	public void configure(JsonObject config, EventLog eventLog, String id, BrowserControl browser, String baseUrl) {
		this.id = id;
		this.eventLog = eventLog;
		this.browser = browser;
		
		env.putString("base_url", baseUrl);
		env.put("config", config);

		require(GenerateServerConfiguration.class);
		exposeEnvString("discoveryUrl");
		exposeEnvString("issuer");
		
		require(CheckServerConfiguration.class);
		
		require(LoadJWKs.class);
		
		
		
		require(GetStaticClientConfiguration.class);

		
		
		this.status = Status.CONFIGURED;
		fireSetupDone();
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#start()
	 */
	@Override
	public void start() {
		// TODO Auto-generated method stub

		
		
		this.status = Status.WAITING;
	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see io.fintechlabs.testframework.testmodule.TestModule#handleHttp(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, javax.servlet.http.HttpSession, org.springframework.util.MultiValueMap, org.springframework.ui.Model)
	 */
	@Override
	public Object handleHttp(String path, HttpServletRequest req, HttpServletResponse res, HttpSession session, JsonObject requestParts) {

		if (path.equals("authorize")) {
			return authorizationEndpoint(requestParts);
		} else if (path.equals("token")) {
			return tokenEndpoint(requestParts);
		} else if (path.equals("jwks")) {
			return jwksEndpoint();
		} else if (path.equals("register")) {
			return registrationEndpoint(requestParts);
		} else if (path.equals("userinfo")) {
			return userinfoEndpoint(requestParts);
		} else if (path.equals(".well-known/openid-configuration")) {
			return discoveryEndpoint();
		} else {
			return new ModelAndView("testError");
		}
		
	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @param m
	 * @return
	 */
	private Object discoveryEndpoint() {
		JsonObject serverConfiguration = env.get("server");
		
		return new ResponseEntity<Object>(serverConfiguration, HttpStatus.OK);
	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @param m
	 * @return
	 */
	private Object userinfoEndpoint(JsonObject requestParts) {

		return null;

	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @param m
	 * @return
	 */
	private Object registrationEndpoint(JsonObject requestParts) {

		//env.put("client_registration_request", requestParts.get("body_json"));
		
		// TODO Auto-generated method stub
		return null;
		
	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @param m
	 * @return
	 */
	private Object jwksEndpoint() {

		JsonObject jwks = env.get("public_jwks");
		
		return new ResponseEntity<Object>(jwks, HttpStatus.OK);
	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @param m
	 * @return
	 */
	private Object tokenEndpoint(JsonObject requestParts) {
		// TODO Auto-generated method stub
		return null;
		
	}

	/**
	 * @param req
	 * @param res
	 * @param params
	 * @param m
	 * @return
	 */
	private Object authorizationEndpoint(JsonObject requestParts) {
		
		env.put("authorization_endpoint_request", requestParts.get("params").getAsJsonObject());

		require(EnsureMatchingClientId.class);
		
		require(EnsureMatchingRedirectUri.class);
		
		// TODO: check scopes
		require(CreateAuthorizationCode.class);
		
		require(RedirectBackToClientWithAuthorizationCode.class);
		
		exposeEnvString("authorization_endpoint_response_redirect");
		
		String redirectTo = env.getString("authorization_endpoint_response_redirect");
		
		return new RedirectView(redirectTo, false, false, false);
	
	}

}
