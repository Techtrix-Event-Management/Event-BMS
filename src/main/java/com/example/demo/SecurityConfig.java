package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.service.CustomFacultyDetailService;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomFacultyDetailService customFacultyDetailService;
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomFacultyDetailService customFacultyDetailService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customFacultyDetailService = customFacultyDetailService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
            		.requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id}", "/api/sponsors/all", "/api/qr/getall").permitAll() // Allow public access
                .requestMatchers(HttpMethod.POST,"/api/auth/signup" , "/api/auth/login", "/api/registered-students/register", "/api/registered-students/register-team").permitAll() 
                .requestMatchers("/api/auth/**", "/api/events/**", "/api/email/send-to-student/{id}", "/api/email/send-to-team/{teamId}", "/api/email/send-to-all", "/api/auth/info", "/api/auth/logout", "/api/auth/{id}/registered-students",
                		"/api/auth/{id}/teams", "/api/auth/{id}/get-student", "/api/auth/search", "/api/auth/teams/search", "/api/events/edit/{id}", "/api/sponsors/add",
                		"/api/sponsors/delete/{id}", "/api/qr/postqr").hasRole("ADMIN")
                .anyRequest().authenticated() // All other requests need authentication
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter
        	System.out.println("security config loaded");
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedHeaders(List.of("Content-Type", "Authorization"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true); // Allow cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
