package net.openid.conformance.condition.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jwt.SignedJWT;
import net.openid.conformance.condition.Condition.ConditionResult;
import net.openid.conformance.condition.ConditionError;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import net.openid.conformance.util.JWTUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.json.Json;

import java.text.ParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class SetDpopAccessTokenHash_UnitTest {

	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private SetDpopAccessTokenHash cond;

	@Before
	public void setUp() throws Exception {

		cond = new SetDpopAccessTokenHash();

		cond.setProperties("UNIT-TEST", eventLog, ConditionResult.INFO);

	}

	@Test
	public void testEvaluate() {

		env.putObject("dpop_proof_claims", new JsonObject());
		// values from example in https://datatracker.ietf.org/doc/html/draft-ietf-oauth-dpop#section-7.1
		env.putString("access_token", "value", "Kz~8mXK1EalYznwH-LC-1fBAo.4Ljp~zsPE_NeO.gxU");

		cond.execute(env);

		assertThat(env.getString("dpop_proof_claims", "ath")).isEqualTo("fUHyO2r2Z3DZ53EsNrWBb0xWXoaNy59IiKCAqksmQEo");

	}


}
