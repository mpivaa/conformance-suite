package net.openid.conformance.condition.client;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import net.openid.conformance.condition.AbstractCondition;
import net.openid.conformance.condition.PostEnvironment;
import net.openid.conformance.condition.PreEnvironment;
import net.openid.conformance.testmodule.Environment;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class FAPIBrazilExtractClientMTLSCertificateSubject extends AbstractFAPIBrazilExtractCertificateSubject {

	@Override
	@PreEnvironment(required = "mutual_tls_authentication")
	@PostEnvironment(required = "certificate_subject")
	public Environment evaluate(Environment env) {
		String certString = env.getString("mutual_tls_authentication", "cert");

		if (Strings.isNullOrEmpty(certString)) {
			throw error("Couldn't find TLS client certificate for MTLS");
		}

		JsonObject certificateSubject = extractSubject(certString, "DCR testing must be done using a BRCAC profile certificate where the subjectdn contains a UID");

		env.putObject("certificate_subject", certificateSubject);

		logSuccess("Extracted subject from MTLS certificate", certificateSubject);

		return env;
	}

}
