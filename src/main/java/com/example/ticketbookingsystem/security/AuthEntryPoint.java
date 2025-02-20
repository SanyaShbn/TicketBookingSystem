//package com.example.ticketbookingsystem.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//
///**
// * Entry point for handling authentication exceptions.
// */
//@Component
//public class AuthEntryPoint implements AuthenticationEntryPoint {
//
//    /**
//     * Handles authentication exceptions by sending an unauthorized status and an error message.
//     *
//     * @param request the HTTP request.
//     * @param response the HTTP response.
//     * @param authException the authentication exception.
//     * @throws IOException if an I/O error occurs.
//     */
//    @Override
//    public void commence(HttpServletRequest request, HttpServletResponse response,
//                         AuthenticationException authException) throws IOException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        PrintWriter writer = response.getWriter();
//        writer.println("Error: " + authException.getMessage());
//    }
//}