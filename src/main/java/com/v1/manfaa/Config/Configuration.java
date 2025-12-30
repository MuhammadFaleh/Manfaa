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
                        .requestMatchers("/api/v1/users/add").permitAll() // ToDo:test only must be removed before production, leave only the repo and model delete controller, service

                        // Admin endpoints
                        .requestMatchers("/api/v1/category/add", "/api/v1/category/update/**", "/api/v1/category/delete/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/credit/get-all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/company/get-all", "/api/v1/company/get-companies-full", "/api/v1/company/delete").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/contract/get-all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/transaction/get-all", "/api/v1/transaction/add-balance", "/api/v1/transaction/refund/**", "/api/v1/transaction/get-by-companyId/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/service-request/get-all-with-bids", "/api/v1/service-request/get-company-requests/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/review/get-all", "/api/v1/review/search/**", "/api/v1/review/exchange-type/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/service-bid/get-all").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/skills/add", "/api/v1/skills/update/**", "/api/v1/skills/delete/**").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/subscriptions/get").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/ticket/get-all", "/api/v1/ticket/resolve", "/api/v1/ticket/reject").hasAuthority("ADMIN")
                        .requestMatchers("/api/v1/ticket/subscriber/**").hasAuthority("ADMIN")

                        // Company endpoints
                        .requestMatchers("/api/v1/credit/get-my-credits").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/contract/create", "/api/v1/contract/delete/**", "/api/v1/contract/accept/**", "/api/v1/contract/reject/**", "/api/v1/contract/complete/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/transaction/get-my-transactions").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/payments/pay").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-request/create-token-request", "/api/v1/service-request/create-barter-request", "/api/v1/service-request/create-either-request").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-request/update/**", "/api/v1/service-request/delete/**", "/api/v1/service-request/get-with-bids/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-request/search", "/api/v1/service-request/get-by-category/**", "/api/v1/service-request/get-by-exchange-type/**", "/api/v1/service-request/get-by-date-range").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/review/add/**", "/api/v1/review/update/**", "/api/v1/review/get/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/review/company/received", "/api/v1/review/company/written", "/api/v1/review/company/reviewed-contracts", "/api/v1/review/company/best-to-worst").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/service-bid/create/**", "/api/v1/service-bid/update/**", "/api/v1/service-bid/delete/**", "/api/v1/service-bid/accept/**", "/api/v1/service-bid/reject/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/skills/assign-skill/**", "/api/v1/skills/remove-skill/**", "/api/v1/skills/get-skills", "/api/v1/skills/search/**").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/subscriptions/monthly", "/api/v1/subscriptions/yearly", "/api/v1/subscriptions/cancel").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/ticket/add-contract/**", "/api/v1/ticket/add-suggestion", "/api/v1/ticket/add-subscription", "/api/v1/ticket/add-platform").hasAuthority("COMPANY")
                        .requestMatchers("/api/v1/ticket/update/**", "/api/v1/ticket/delete/**", "/api/v1/ticket/my-tickets", "/api/v1/ticket/my-tickets/status/**").hasAuthority("COMPANY")

                        // Both ADMIN and COMPANY endpoints
                        .requestMatchers("/api/v1/category/get").hasAnyAuthority("ADMIN", "COMPANY")
                        .requestMatchers("/api/v1/company/update/**", "/api/v1/company/get-company-full", "/api/v1/company/get-company-id-full/**").hasAnyAuthority("ADMIN", "COMPANY")
                        .requestMatchers("/api/v1/service-request/get-requests").hasAnyAuthority("ADMIN", "COMPANY")
                        .requestMatchers("/api/v1/service-request/get-requests-subscriber").hasAnyAuthority("ADMIN", "COMPANY")
                        .requestMatchers("/api/v1/service-request/get-with-bids-subscribers/**").hasAnyAuthority("ADMIN", "COMPANY")
                        .requestMatchers("/api/v1/review/delete/**").hasAnyAuthority("ADMIN", "COMPANY")
                        .requestMatchers("/api/v1/skills/get").hasAnyAuthority("ADMIN", "COMPANY")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
