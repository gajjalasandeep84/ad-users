package com.example.adusersdemo.controller;

import com.example.adusersdemo.model.AdUser;
import com.example.adusersdemo.service.AdUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdUserController {

    @Autowired
    private AdUserService adUserService;

    @GetMapping("/ad/users")
    public List<AdUser> listUsers() {
        return adUserService.getAllUsers();
    }
}
