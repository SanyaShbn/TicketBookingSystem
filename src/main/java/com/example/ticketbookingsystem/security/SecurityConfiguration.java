package com.example.ticketbookingsystem.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.example.ticketbookingsystem.entity.Role.ADMIN;
import static com.example.ticketbookingsystem.entity.Role.USER;

/**
 * Security configuration class for the Ticket Booking System application.
 * It manages the security settings, including authentication, authorization,
 * and password encoding.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationFilter authenticationFilter;

    private final AuthEntryPoint exceptionHandler;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception  {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        config.applyPermitDefaultValues();

        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Configures the security filter chain.
     *
     * @param http The HttpSecurity object to be configured.
     * @return The SecurityFilterChain instance.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(CsrfConfigurer:: disable)
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/registration", "/v3/api-docs/*",
                                "/swagger-ui/*").permitAll()
                        .requestMatchers(request -> request.getParameter("lang") != null).permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority(ADMIN.getAuthority())
                        .requestMatchers("/api/user_cart",
                                         "/api/purchase",
                                         "/api/purchasedTickets").hasAuthority(USER.getAuthority())
                        .anyRequest().authenticated()
                )
                .exceptionHandling((ex) -> ex
                        .authenticationEntryPoint(exceptionHandler)
                ).addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);;

        return http.build();
    }

}