package com.example.adusersdemo.component;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class LdapHealthIndicator implements HealthIndicator{
    	private final LdapTemplate ldapTemplate;

    public LdapHealthIndicator(@Qualifier("testLdapTemplate") LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    @Override
    public Health health() {
        try {
            // Perform a simple LDAP query to check connectivity
            ldapTemplate.search("", "(objectClass=*)", (AttributesMapper<String>) (attrs) -> "ok");
            return Health.up().withDetail("ldap", "reachabled").build();
        } catch (Exception e) {
            return Health.down(e).withDetail("ldap", "unreachable").build();
        }
    }
}
