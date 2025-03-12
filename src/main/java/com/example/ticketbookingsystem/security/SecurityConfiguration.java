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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

import static com.example.ticketbookingsystem.entity.Role.ADMIN;
import static com.example.ticketbookingsystem.entity.Role.USER;
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration class for the Ticket Booking System application.
 * It manages the security settings, including authentication, authorization,
 * and password encoding.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private static final String[] AUTH_WHITELIST = {
            "/api/v1/login",
            "/api/v1/registration",
            "/api/v1/refresh",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/**"
    };

    private static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");

    private static final String CSRF_TOKEN_HEADER = "X-XSRF-TOKEN";

    private static final List<String> ALLOWED_HEADERS = List.of(
            "Authorization",
            "Cache-Control",
            "Content-Type",
            CSRF_TOKEN_HEADER);

    private static final String ADMIN_ROLE_PATH = "/api/v1/admin/**";

    private static final String USER_CART_PATH = "/api/v1/user_cart";

    private static final String PURCHASES_PATH = "/api/v1/purchases/**";

    private static final String CORS_ALLOWED_ORIGIN = "http://localhost:4200";

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthenticationFilter authenticationFilter;

    private final AuthEntryPoint exceptionHandler;

    /**
     * Configures the global authentication manager.
     *
     * @param auth the authentication manager builder.
     * @throws Exception if an error occurs during configuration.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception  {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    /**
     * Configures the authentication manager.
     *
     * @param userDetailsService the user details service.
     * @return the authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());

        return new ProviderManager(authenticationProvider);
    }

    /**
     * Configures CORS settings for supposed origin server, where client's part may be hosted.
     *
     * @return the CORS configuration source.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Collections.singletonList(CORS_ALLOWED_ORIGIN));
        config.setAllowedMethods(ALLOWED_METHODS);
        config.setAllowedHeaders(ALLOWED_HEADERS);
        config.setExposedHeaders(Collections.singletonList(CSRF_TOKEN_HEADER));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
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

        http.csrf((csrf) -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .cors(withDefaults())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(request -> request.getParameter("lang") != null).permitAll()
                        .requestMatchers(ADMIN_ROLE_PATH).hasRole(ADMIN.getAuthority())
                        .requestMatchers(USER_CART_PATH,
                                PURCHASES_PATH).hasRole(USER.getAuthority())
                        .anyRequest().authenticated()
                )
                .exceptionHandling((ex) -> ex
                        .authenticationEntryPoint(exceptionHandler)
                ).addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}