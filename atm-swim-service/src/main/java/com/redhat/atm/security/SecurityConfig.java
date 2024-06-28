package com.redhat.atm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final String ADMIN = "admin";
    public static final String USER = "user";
    private final JwtConverter jwtConverter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authz) -> authz

                        .requestMatchers(EndpointRequest.to("health")).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/subscription/v1/ping").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/subscription/v1/admin/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/subscription/v1/admin/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST,"/api/queue/management/v1/admin/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/subscription/v1/user/**").hasRole(USER)
                        .requestMatchers(HttpMethod.PUT, "/api/subscription/v1/user/**").hasRole(USER)
                        .requestMatchers(HttpMethod.GET, "/api/subscription/v1/admin-and-user/**").hasAnyRole(ADMIN,USER)
                        .requestMatchers(HttpMethod.GET,"/api/messagelog/v1/**").permitAll()
                        .anyRequest().authenticated());

        http.sessionManagement(sess -> sess.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS));
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)));

        return http.build();
    }
}
