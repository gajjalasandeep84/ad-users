package com.example.adusersdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AdUsersDemoApplication {
	
	
    public static void main(String[] args) {
        SpringApplication.run(AdUsersDemoApplication.class, args);
    }
    
    
}
