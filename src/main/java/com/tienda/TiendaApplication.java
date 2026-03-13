package com.tienda;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TiendaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TiendaApplication.class, args);
    }

    @Bean
    CommandLineRunner testPassword(PasswordEncoder passwordEncoder) {
        return args -> {
            System.out.println("HASH_ADMIN123 = " + passwordEncoder.encode("admin123"));
        };
    }
}
