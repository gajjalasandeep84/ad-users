package com.example.adusersdemo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.adusersdemo.model.AdUser;
import com.example.adusersdemo.service.AdUserService;

@RestController
public class AdUserController {

	@Autowired
	private AdUserService adUserService;

	/*
	 * @GetMapping("/ad/users") public List<AdUser> listUsers() { return
	 * adUserService.getAllDevTestUsers(); }
	 * 
	 * @GetMapping("/ad/users/{env}") public List<AdUser> listUsers(@PathVariable
	 * String env) { return adUserService.getAllUsersForEnv(env); }
	 */

	@GetMapping("/ad/usersByEnv/{env}")
	public List<AdUser> listUsersByEnv(@PathVariable String env) {
		return adUserService.getUsers(env);
	}

}
