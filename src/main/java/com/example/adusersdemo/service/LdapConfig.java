package com.example.adusersdemo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

	private LdapContextSource buildCtx(String url, String base, String user, String pass) {
		LdapContextSource ctx = new LdapContextSource();
		ctx.setUrl(url);
		ctx.setBase(base);
		ctx.setUserDn(user);
		ctx.setPassword(pass);
		ctx.afterPropertiesSet();
		return ctx;
	}

	@Bean("testLdapTemplate")
	public LdapTemplate testTemplate(@Value("${ad.test.url}") String url, @Value("${ad.test.base}") String base,
			@Value("${ad.test.user}") String user, @Value("${ad.test.pass}") String pass) {
		return new LdapTemplate(buildCtx(url, base, user, pass));
	}

	@Bean("prodLdapTemplate")
	public LdapTemplate prodTemplate(@Value("${ad.prod.url}") String url, @Value("${ad.prod.base}") String base,
			@Value("${ad.prod.user}") String user, @Value("${ad.prod.pass}") String pass) {
		return new LdapTemplate(buildCtx(url, base, user, pass));
	}
}
