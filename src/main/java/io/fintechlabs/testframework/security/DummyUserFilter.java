package io.fintechlabs.testframework.security;

import com.google.common.collect.ImmutableSet;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class DummyUserFilter extends GenericFilterBean {

	@Value("${fintechlabs.devmode:false}")
	private boolean devmode;

	private static Logger logger = LoggerFactory.getLogger(DummyUserFilter.class);
	private static Set<GrantedAuthority> authorities = ImmutableSet.of(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
	private static OIDCAuthenticationToken token = new OIDCAuthenticationToken("developer","developer.com",
			null, authorities,null, null, null);


	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		logger.debug("In Dummy Filter doFilter()");
		if (devmode) {
			SecurityContextHolder.getContext().setAuthentication(token);
		}
		chain.doFilter(request,response);
	}
}
