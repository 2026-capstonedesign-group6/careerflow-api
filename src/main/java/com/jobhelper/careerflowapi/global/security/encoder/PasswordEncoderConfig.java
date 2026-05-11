package com.jobhelper.careerflowapi.global.security.encoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = Map.of(
                "bcrypt", new BCryptPasswordEncoder(12),
                "argon2", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()
        );

        return new DelegatingPasswordEncoder("bcrypt", encoders);
    }
}
