//package com.example.ticketbookingsystem.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
///**
// * Filter for JWT authentication.
// */
//@Component
//@RequiredArgsConstructor
//public class AuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    /**
//     * Filters incoming requests for JWT authentication.
//     *
//     * @param request the HTTP request.
//     * @param response the HTTP response.
//     * @param filterChain the filter chain.
//     * @throws ServletException if a servlet error occurs.
//     * @throws IOException if an I/O error occurs.
//     */
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//
//        String jws = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (jws != null) {
//            Authentication authentication = jwtService.getAuthentication(request);
//
//            if (authentication != null) {
//                SecurityContextHolder.getContext().setAuthentication(authentication);
//            }
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}