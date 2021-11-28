package net.openid.conformance.ekyc.condition.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tananaev.jsonpatch.JsonPatch;
import com.tananaev.jsonpatch.JsonPatchFactory;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class AddVerifiedClaimsFromUserinfoToAuthorizationEndpointRequest_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private AddVerifiedClaimsFromUserinfoToAuthorizationEndpointRequest cond;

	@Before
	public void setUp() throws Exception {
		cond = new AddVerifiedClaimsFromUserinfoToAuthorizationEndpointRequest();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
	}

	protected void runTest(String userInfoFilename, String expectedRequestFilename) throws IOException {
		String testUserInfoJson = IOUtils.resourceToString(userInfoFilename, StandardCharsets.UTF_8, getClass().getClassLoader());
		env.putObjectFromJsonString("config", "ekyc_userinfo", testUserInfoJson);
		env.putObject("authorization_endpoint_request", new JsonObject());
		cond.execute(env);
		JsonObject result = env.getObject("authorization_endpoint_request");

		String expectedJson = IOUtils.resourceToString(expectedRequestFilename, StandardCharsets.UTF_8, getClass().getClassLoader());
		JsonObject expected = (JsonObject) new JsonParser().parse(expectedJson);

		JsonPatchFactory jpf = new JsonPatchFactory();
		JsonPatch patch = jpf.create(expected, result);

		System.out.println("patch: " + patch.toString());
		assertThat(patch.size()).isEqualTo(0);
	}

	@Test
	public void testEvaluate_noError() throws Exception {
		String userInfoFilename = "test-user-info.json";
		String expectedRequestFilename = "verified-claims-request-based-on-userinfo.json";

		runTest(userInfoFilename, expectedRequestFilename);
	}

	@Test
	public void testEvaluate_noErrorYesTest001() throws Exception {
		String userInfoFilename = "test-user-info-yes-test001.json";
		String expectedRequestFilename = "verified-claims-request-based-on-userinfo-yes-test001.json";

		runTest(userInfoFilename, expectedRequestFilename);
	}

}
