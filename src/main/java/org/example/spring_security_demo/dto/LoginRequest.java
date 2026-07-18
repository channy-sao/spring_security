package org.example.spring_security_demo.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
