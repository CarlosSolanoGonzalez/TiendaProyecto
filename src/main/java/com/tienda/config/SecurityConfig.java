package com.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider) throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/home", "/usuarios/login", "/usuarios/registro",
                        "/css/**", "/js/**", "/img/**", "/webjars/**", "/consulta/**")
                .permitAll()
                .requestMatchers("/categorias/**", "/productos/nuevo", "/productos/*/editar")
                .hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/categorias/**")
                .hasRole("ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/productos/**")
                .hasRole("ADMIN")
                .requestMatchers("/carrito/**")
                .hasAnyRole("ADMIN", "CLIENTE", "CLIENT")
                .anyRequest()
                .permitAll())
                .formLogin(form -> form
                .loginPage("/usuarios/login")
                .loginProcessingUrl("/login")
                .usernameParameter("correo")
                .passwordParameter("contrasena")
                .defaultSuccessUrl("/", true)
                .failureUrl("/usuarios/login?error")
                .permitAll())
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/usuarios/login?logout")
                .permitAll())
                .rememberMe(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}
