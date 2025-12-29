package com.v1.manfaa.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@org.springframework.context.annotation.Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class Configuration {
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(//react url once we add it
                ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/payments/callback").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/company/register").permitAll()
                        .requestMatchers("/api/v1/users/add").permitAll()

                        .requestMatchers("/api/v1/transaction/get-my-transactions").hasAuthority("COMPANY")

                        // admin endpoints
                        .requestMatchers("/api/v1/service-bid/get-all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/credit/get-all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/transaction/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/company/get-all","/api/v1/company/get-companies-full","/api/v1/company/delete").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/contract/get-all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/category/add/**", "/api/v1/category/update/**", "/api/v1/category/delete/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/skills/update/**", "/api/v1/skills/add", "/api/v1/skills/delete/**").hasAuthority("ADMIN")

                        // company endpoints
                        .requestMatchers("/api/v1/service-request/create-token-request").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/credit/get-my-credits").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-request/get-requests").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-request/get-with-bids/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/company/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-bid/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/contract/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/category/get").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/skills/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-request/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/payments/card").hasAuthority("COMPANY")

                        // subscription endpoints
                        .requestMatchers("/api/v1/subscriptions/**").hasAuthority("COMPANY")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
