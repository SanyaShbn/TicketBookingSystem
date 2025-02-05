package com.example.ticketbookingsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

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

    /**
     * Configures the password encoder bean.
     *
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/registration", "/css/**", "/js/**", "/WEB-INF/jsp/**").permitAll()
                        .requestMatchers(request -> request.getParameter("lang") != null).permitAll()
                        .requestMatchers("/admin/**").hasAuthority(ADMIN.getAuthority())
                        .requestMatchers("/view_available_events",
                                         "/view_available_tickets",
                                         "/user_cart",
                                         "/purchase",
                                         "/purchasedTickets").hasAuthority(USER.getAuthority())
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login")
                        .successHandler(myAuthenticationSuccessHandler())
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    /**
     * Configures the authentication success handler bean.
     *
     * @return An instance of MyAuthenticationSuccessHandler.
     */
    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new MyAuthenticationSuccessHandler();
    }

    /**
     * Custom authentication success handler that redirects users
     * based on their roles after successful authentication.
     */
    public static class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication) throws IOException {
            String redirectUrl = "/view_available_events";

            for (var authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ADMIN")) {
                    redirectUrl = "/admin";
                    break;
                }
            }

            response.sendRedirect(redirectUrl);
        }
    }

}