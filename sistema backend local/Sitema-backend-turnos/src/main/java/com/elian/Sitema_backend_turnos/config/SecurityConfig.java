package com.elian.Sitema_backend_turnos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults());

        http.headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {      //evita guardar contraseñas en texto plano
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService() {

        UserDetails admin = User
                .withUsername("admin")
                .password("{noop}admin123")
                .roles("ADMIN")
                .build();

        UserDetails profesional = User
                .withUsername("pro")
                .password("{noop}pro123")
                .roles("PROFESIONAL")
                .build();

        return new InMemoryUserDetailsManager(admin, profesional);
    }
}
