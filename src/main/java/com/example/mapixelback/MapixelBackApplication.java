package com.example.mapixelback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.example.mapixelback","com.example.mapixelback.springSecurity"})

public class MapixelBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MapixelBackApplication.class, args);
    }

}
