package com.example.adusersdemo.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

@Component
public class LdapRouter {

	private final LdapTemplate testTemplate;
	private final LdapTemplate prodTemplate;

	@Value("${ad.test.searchBase}")
	private String testSearchBase;
	@Value("${ad.prod.searchBase}")
	private String prodSearchBase;

	public LdapRouter(@Qualifier("testLdapTemplate") LdapTemplate testTemplate,
			@Qualifier("prodLdapTemplate") LdapTemplate prodTemplate) {
		this.testTemplate = testTemplate;
		this.prodTemplate = prodTemplate;
	}

	public LdapTemplate forEnv(String env) {
		if ("test".equalsIgnoreCase(env))
			return testTemplate;
		if ("prod".equalsIgnoreCase(env))
			return prodTemplate;
		throw new IllegalArgumentException("Unknown env: " + env);
	}

	public String searchBaseForEnv(String env) {
		if ("test".equalsIgnoreCase(env))
			return testSearchBase;
		if ("prod".equalsIgnoreCase(env))
			return prodSearchBase;
		throw new IllegalArgumentException("Unknown env: " + env);
	}
}
