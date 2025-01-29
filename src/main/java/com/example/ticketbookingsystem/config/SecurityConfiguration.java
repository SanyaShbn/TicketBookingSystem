package com.example.ticketbookingsystem.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

import static com.example.ticketbookingsystem.entity.Role.ADMIN;
import static com.example.ticketbookingsystem.entity.Role.USER;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/WEB-INF/jsp/**", "/changeLanguage").permitAll()
                        .requestMatchers("/registration").permitAll()
                        .requestMatchers("/admin/**").hasRole(ADMIN.getAuthority())
                        .requestMatchers("/view_available_events",
                                         "/view_available_tickets",
                                         "/user_cart",
                                         "/purchase").hasRole(USER.getAuthority())
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

    @Bean
    public AuthenticationSuccessHandler myAuthenticationSuccessHandler() {
        return new MyAuthenticationSuccessHandler();
    }

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