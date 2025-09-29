package com.example.adusersdemo.model;

import java.util.List;

import lombok.Data;

@Data
public class AdUser {
    private String accountExpires;
    private String displayName;
    private String mail;
    private List<String> memberOf;
    private String name;
    private String pwdLastSet;
    private String sAMAccountName;
    private String whenChanged;
    private String whenCreated;
}
