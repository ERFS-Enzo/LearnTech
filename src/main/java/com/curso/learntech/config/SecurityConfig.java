package com.curso.learntech.config;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("perfis");
        authorities.setAuthorityPrefix("ROLE_");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationConverter converter) throws Exception {

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/health",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui.html",
                                "/swagger-ui/**")
                        .permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/autenticacao/login")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contas-acesso")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(
                                "/api/contas-acesso/*/perfis",
                                "/api/contas-acesso/*/status")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/pessoas/*/status")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/**")
                        .hasAnyRole("OPERADOR", "ADMINISTRADOR")
                        .anyRequest().denyAll())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt ->
                        jwt.jwtAuthenticationConverter(converter)))
                .build();
    }
}