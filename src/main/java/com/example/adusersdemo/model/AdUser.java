package com.example.adusersdemo.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
//Testing
@Data
public class AdUser {
	private String accountExpires;
	private String displayName;
	private String mail;
	private List<String> memberOf;
	private String name;
	private LocalDateTime pwdLastSet;
	private String sAMAccountName;
	private LocalDateTime whenChanged;
	private LocalDateTime whenCreated;
}
