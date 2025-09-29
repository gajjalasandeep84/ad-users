package com.example.adusersdemo.service;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;

import com.example.adusersdemo.model.AdUser;

@Service
public class AdUserService {

	@Autowired
	private LdapTemplate ldapTemplate;

	@Value("${ldap.search.base}")
	private String searchBase;

	public List<AdUser> getAllUsers() {
		return ldapTemplate.search(searchBase, "(objectClass=user)",
				(AttributesMapper<AdUser>) this::mapUser);
	}

	private AdUser mapUser(Attributes attrs) throws javax.naming.NamingException {
		AdUser user = new AdUser();

		user.setAccountExpires(getString(attrs, "accountExpires"));
		user.setDisplayName(getString(attrs, "displayName"));
		user.setMail(getString(attrs, "mail"));
		user.setName(getString(attrs, "name"));
		user.setPwdLastSet(getString(attrs, "pwdLastSet"));
		user.setSAMAccountName(getString(attrs, "sAMAccountName"));
		user.setWhenChanged(getString(attrs, "whenChanged"));
		user.setWhenCreated(getString(attrs, "whenCreated"));

		List<String> groups = new ArrayList<>();
		Attribute memberOfAttr = attrs.get("memberOf");
		if (memberOfAttr != null) {
			NamingEnumeration<?> all = memberOfAttr.getAll();
			while (all.hasMore()) {
				String fullDn = (String) all.next();
				if (fullDn.startsWith("CN=")) {
					groups.add(fullDn.split(",")[0]);
				} else {
					groups.add(fullDn);
				}
			}
		}
		user.setMemberOf(groups);

		return user;
	}

	private String getString(Attributes attrs, String name) throws javax.naming.NamingException {
		Attribute attr = attrs.get(name);
		return attr != null ? attr.get().toString() : null;
	}
}
