package com.example.spring_security_jwt.security;

import org.springframework.beans.factory.annotation.Value;

public class JwtUtil {

    @Value("${jwt_secret}")
    private String secret;

}
