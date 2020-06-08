package net.openid.conformance.condition.as;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.openid.conformance.condition.Condition;
import net.openid.conformance.logging.TestInstanceEventLog;
import net.openid.conformance.testmodule.Environment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ExtractClientCertificateFromTokenEndpointRequestHeaders_UnitTest {
	@Spy
	private Environment env = new Environment();

	@Mock
	private TestInstanceEventLog eventLog;

	private JsonObject tokenEndpointRequest;
	private ExtractClientCertificateFromTokenEndpointRequestHeaders cond;


    @Before
    public void setUp() throws Exception {
		cond = new ExtractClientCertificateFromTokenEndpointRequestHeaders();
		cond.setProperties("UNIT-TEST", eventLog, Condition.ConditionResult.INFO);
		JsonObject sampleHeaders = new JsonObject();

		String certificate = "-----BEGIN CERTIFICATE-----\n" +
			"MIIGCDCCA/CgAwIBAgIJAIgwloUBq+0LMA0GCSqGSIb3DQEBCwUAMGYxCzAJBgNV\n" +
			"BAYTAlVTMQswCQYDVQQIDAJDQTESMBAGA1UEBwwJU2FuIFJhbW9uMQ0wCwYDVQQK\n" +
			"DARPSURGMScwJQYDVQQDDB50ZXN0LmNlcnRpZmljYXRpb24uZXhhbXBsZS5jb20w\n" +
			"HhcNMjAwNTIxMDYzNTAyWhcNMzAwNTE5MDYzNTAyWjBmMQswCQYDVQQGEwJVUzEL\n" +
			"MAkGA1UECAwCQ0ExEjAQBgNVBAcMCVNhbiBSYW1vbjENMAsGA1UECgwET0lERjEn\n" +
			"MCUGA1UEAwwedGVzdC5jZXJ0aWZpY2F0aW9uLmV4YW1wbGUuY29tMIICIjANBgkq\n" +
			"hkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAwFCUwv8D5ReG/bXSiHSuLQ0laRG+paST\n" +
			"PlXLhflpGh4pWsHoPaLHmIwScXKDYZVUGtvLzQkkFT6QODn3FXWogxkzNCLpbpvk\n" +
			"iiHK/1NdibYeKaWj0CnaFj6v5ObpsMo5QpQkCPlOPaARGjnsNoEVBhqFdbp4xqiw\n" +
			"6ZmBBNYt8iZ8D9ta50UJmwYPyOiwyacKni5XBjfbjq7M4pubHSg1fgzDxlJdSEKa\n" +
			"n9nNMTLRXor3qvJLUpbDR4x+NjKfezlpeKI3Y0GyEdB0DxOYJSOgF8wq6gSMTr7F\n" +
			"SAVacXRKIxyzAICmFZj4wYGQ2wYhQXB+wgbMw9zmD25NlwqTHQLcN9fi2Ew/Abvu\n" +
			"J79OoMDLZrbInXKtPObv0Szz7A2tBv4O6juQRlkTfs27z1K1hFr+MKzXNd4vgGVv\n" +
			"PUIdluKZzxx5qGcg/ya07LRl4qn06zm+ectdCeKUuFQi3q0YVE3NSRXgv2VTpzWb\n" +
			"5zy4iJlxifiInp3miiYP9kfRKR7DBLWV6/RsrObmgJUeUjiwFEfimaeM5NFKYwdE\n" +
			"TLhAB/1L28EevM5k6jdRfDHsqGbEL5T630G6iwu1Ih+N7GNVobkJuzOYPtwyxRwN\n" +
			"8vcVVCvQ2k41sNbpnacg8SyRBepeiIQR+xC1avvfF148tUwnhz1vQqR4Cx3Pbrsh\n" +
			"+V21WCWsH9MCAwEAAaOBuDCBtTCBsgYDVR0RBIGqMIGngg93d3cuZXhhbXBsZS5j\n" +
			"b22CIGRuc25hbWUyLmNvbmZvcm1hbmNlLmV4YW1wbGUuY29thwR/AAABhwQKAAAB\n" +
			"hiRodHRwczovL3d3dy5jZXJ0aWZpY2F0aW9uLm9wZW5pZC5uZXSGEWh0dHBzOi8v\n" +
			"bG9jYWxob3N0gRhjZXJ0aWZpY2F0aW9uQG9wZW5pZC5uZXSBE2pvaG5kb2VAZXhh\n" +
			"bXBsZS5jb20wDQYJKoZIhvcNAQELBQADggIBAENIqUWJgwFq9eXWkM3yWZ1p2HqV\n" +
			"e0dAwCBPMT1wUQ8OdzPgR9AzZxAhv3uqHmCqEY8eeFXQyMgz9lNPjTvnzVQxAFH4\n" +
			"SqH20S3mh/ymMSMaZsHb8/acziXtY6qtTpwwjJmp9szx+fXlMrssr51HAivbI1ea\n" +
			"PI2PzpwgHJIlthg5DSbvoYhNuvUtv33N9zzOcFTBLcGcdLXeVisnCXMmltweyUM3\n" +
			"AKqT9eMWZfxCMg69eFPNs9nvQ1u9BQqPYns2illfFdtL4hN6S6v4WjUiUS2IEmmJ\n" +
			"h8k9xLHwb8ZQucdOb3V4ybHGqx7/aigHOcvpbUL+4aAuzyVtU20QR3wQXJ9dlRyk\n" +
			"kpz+RJLH2h45JaWtS4T0dv+NmATXjIcpEqRMDRpZT4y35wkMfX375CBaV0OAm+0T\n" +
			"1DvD9NFa2HQUQTV/vedJIXavF0yswuaPyY8sKpH4v66FpZJYpO8K1O+JGM3/sGgE\n" +
			"X3LhcekIOnSZzwBraRian7u1fJhfSmAUlBcnaxtMpZ2XVGvtedz+NA9lkXvD9RI9\n" +
			"u/9XJsTKaQn35FbGG9W18bm9JkB9IU08hekygqSJK9v/6ajD6S1U5SBgrQ7GfQya\n" +
			"ygd6HbUDO51D2El4vqxBcbMlzPEItj11b0xaBM1ZuF64/s3BzzwRyuyTOU5gu4SL\n" +
			"PjBLfDw2pK+NfvAb\n" +
			"-----END CERTIFICATE-----\n";
		sampleHeaders.addProperty("x-ssl-cert", certificate);

		tokenEndpointRequest = new JsonObject();
		tokenEndpointRequest.add("headers", sampleHeaders);

    }

    @Test
    public void evaluate() throws InvalidNameException
	{
		env.putObject("token_endpoint_request", tokenEndpointRequest);

		cond.execute(env);

		assertTrue(env.containsObject("client_certificate"));

		LdapName ldapNameActual = new LdapName(env.getString("client_certificate", "subject.dn"));
		LdapName ldapNameExpected = new LdapName("cn=test.certification.example.com,o=oidf,l=san ramon,st=ca,c=us");
		assertTrue(ldapNameActual.getRdns().size() == ldapNameExpected.getRdns().size());
		assertTrue(ldapNameActual.getRdns().containsAll(ldapNameExpected.getRdns()));

		JsonObject cert = env.getObject("client_certificate");

		assertTrue(cert.has("sanDnsNames"));
		JsonArray sanDnsNames = cert.getAsJsonArray("sanDnsNames");
		assertTrue(sanDnsNames.contains(new JsonPrimitive("www.example.com")));

		assertTrue(cert.has("sanUris"));
		JsonArray sanUris = cert.getAsJsonArray("sanUris");
		assertTrue(sanUris.contains(new JsonPrimitive("https://www.certification.openid.net")));

		assertTrue(cert.has("sanIPs"));
		JsonArray sanIPs = cert.getAsJsonArray("sanIPs");
		assertTrue(sanIPs.contains(new JsonPrimitive("127.0.0.1")));

		assertTrue(cert.has("sanEmails"));
		JsonArray sanEmails = cert.getAsJsonArray("sanEmails");
		assertTrue(sanEmails.contains(new JsonPrimitive("certification@openid.net")));

    }
}
