package net.openid.conformance.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.models.external.OBBrasilConsentPermissions;
import net.openid.conformance.models.external.OpenBankingBrasilConsentRequest;
import net.openid.conformance.testmodule.Environment;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class FAPIBrazilCreateConsentRequest extends AbstractCondition {

	@Override
	@PreEnvironment()
	@PostEnvironment(required = "consent_endpoint_request")
	public Environment evaluate(Environment env) {

		String cpf = env.getString("config", "resource.brazilCpf");
		if (Strings.isNullOrEmpty(cpf)) {
			throw error("CPF value missing from test configuration");
		}

		String[] permissions = Optional.ofNullable(env.getString("consent_permissions"))
			.map(c -> c.split("\\W"))
			.orElse(new String[] {"ACCOUNTS_READ"});

		// see https://openbanking-brasil.github.io/areadesenvolvedor/#direitos-creditorios-descontados-parcelas-do-contrato
		OpenBankingBrasilConsentRequest consentRequest =
			new OpenBankingBrasilConsentRequest(cpf, permissions);
		JsonObject requestObject = consentRequest.toJson();

		env.putObject("consent_endpoint_request", requestObject);

		logSuccess(args("consent_endpoint_request", requestObject));

		return env;
	}

}
