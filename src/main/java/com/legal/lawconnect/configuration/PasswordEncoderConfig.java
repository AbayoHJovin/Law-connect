package com.legal.lawconnect.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {  // Renamed to avoid conflict
    @Bean
    public PasswordEncoder passwordEncoder() {  // Returns interface, implements BCrypt
        return new BCryptPasswordEncoder();
    }
}