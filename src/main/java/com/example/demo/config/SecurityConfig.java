package com.example.demo.config;

import com.example.demo.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private final CustomUserDetailsService userDetailsService;
    public SecurityConfig(CustomUserDetailsService userDetailsService)
    {
        this.userDetailsService = userDetailsService;
    }
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors->cors.configurationSource(corsConfigurationSource()))  //Enable cors for react
                .csrf(csrf->csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(authz->authz
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/register/hospitalStaff").hasRole("ADMIN")
                        .requestMatchers("/api/auth/register/doctor").hasRole("ADMIN")
                        .requestMatchers("/api/auth/register/patient").permitAll()
                        .requestMatchers("/api/auth/createHospital").hasRole("ADMIN")
                        .requestMatchers("/api/patient/payment/webhook").permitAll()
                        .requestMatchers("/api/auth/getHospitals").hasAnyRole("ADMIN","PATIENT")
                        .requestMatchers("/api/doctor/getAppointments").hasAnyRole("DOCTOR","PATIENT")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/doctor/**").hasRole("DOCTOR")
                        .requestMatchers("/api/patient/**").hasRole("PATIENT")
                        .requestMatchers("/api/HospitalStaff/**").hasRole("HOSPITALSTAFF")
                        .requestMatchers("/login").denyAll()
                        .anyRequest().authenticated() //Other endpoints require login
                )
                .formLogin(form->form
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            List<String> roles = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
                            String jsonResponse = String.format(
                                    "{\"message\": \"Login successful\", \"username\": \"%s\", \"roles\": %s}",
                                    username,roles
                            );
                            response.setStatus(200);
                            response.setContentType("application/json");
                            response.getWriter().write(jsonResponse);
                        })
                        .failureHandler((request, response, exception) -> {
                          response.setStatus(401);
                          response.getWriter().write("{\"error\": \"Invalid username or password\"}");
                          response.setContentType("application/json");
                        })
                        .permitAll()
                ).logout(logout->logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200); // Return 200 OK
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                            response.setContentType("application/json");
                        })
                        .permitAll()
                )
                .userDetailsService(userDetailsService);
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }
}
