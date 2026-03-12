package com.example.adusersdemo.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Service;

import com.example.adusersdemo.model.AdUser;
import com.example.adusersdemo.util.AdTimeUtil;

@Service
public class AdUserService {

	private static final DateTimeFormatter AD_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.S'Z'");
	private final LdapRouter router;

	public AdUserService(LdapRouter router) {
		this.router = router;
	}

	public List<AdUser> getUsers(String env) {
		String filter = "(&(objectClass=user)(!(objectClass=computer)))";
		String searchBase = router.searchBaseForEnv(env);
		return router.forEnv(env).search(searchBase, filter, (AttributesMapper<AdUser>) this::mapUser);
	}

	private AdUser mapUser(Attributes attrs) throws javax.naming.NamingException {
		AdUser user = new AdUser();

		user.setDisplayName(getString(attrs, "displayName"));
		user.setMail(getString(attrs, "mail"));
		user.setName(getString(attrs, "name"));
		user.setSAMAccountName(getString(attrs, "sAMAccountName"));
		if (attrs.get("accountExpires") != null) {
			String raw = attrs.get("accountExpires").get().toString();
			user.setAccountExpires(AdTimeUtil.parseAccountExpires(raw));
		}
		if (attrs.get("pwdLastSet") != null) {
			String raw = attrs.get("pwdLastSet").get().toString();
			user.setPwdLastSet(AdTimeUtil.fromFileTime(raw));
		}
		if (attrs.get("whenCreated") != null) {
			String raw = attrs.get("whenCreated").get().toString();
			user.setWhenCreated(LocalDateTime.parse(raw, AD_DATE_FORMAT));
		}
		if (attrs.get("whenChanged") != null) {
			String raw = attrs.get("whenChanged").get().toString();
			user.setWhenChanged(LocalDateTime.parse(raw, AD_DATE_FORMAT));
		}
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

	public static LocalDateTime fromFileTime(String fileTimeStr) {
		long fileTime = Long.parseLong(fileTimeStr);
		if (fileTime == 0 || fileTime == Long.MAX_VALUE) {
			return null; // Never expires
		}
		long millis = (fileTime / 10000L) - 11644473600000L; // convert to Unix epoch
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
	}

	private String getString(Attributes attrs, String name) throws javax.naming.NamingException {
		Attribute attr = attrs.get(name);
		return attr != null ? attr.get().toString() : null;
	}
}
