package com.example.mapixelback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserAuth {
    private String email;
    private String password;
}
