package com.example.ticketbookingsystem.filter;

import com.example.ticketbookingsystem.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    private static final Set<String> PUBLIC_PATH = Set.of("/login", "/registration", "/logout", "/css/", "/js/");
    private static final Map<String, Set<String>> ROLE_URL_MAP = Map.of(
            "ADMIN", Set.of("/admin", "/admin/*"),
            "USER", Set.of("/view_available_events", "/view_available_tickets")
    );
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        var uri = request.getRequestURI();
        var user = (User) request.getSession().getAttribute("user");
        var role = (String) request.getSession().getAttribute("role");

        if (isPublicPath(uri) || (user != null && isRoleAllowed(role, uri))) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else if (user == null) {
            response.sendRedirect("/login");
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
    private boolean isPublicPath(String uri) {
        return PUBLIC_PATH.stream().anyMatch(uri::startsWith);
    }
    private boolean isRoleAllowed(String role, String uri) {
        if (role == null) {
            return false;
        }
        return ROLE_URL_MAP.getOrDefault(role, Set.of()).stream().anyMatch(uri::startsWith);
    }
}