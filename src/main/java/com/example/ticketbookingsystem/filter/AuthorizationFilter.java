package com.example.ticketbookingsystem.filter;

import com.example.ticketbookingsystem.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * AuthorizationFilter is a servlet filter that handles authorization checks
 * for incoming HTTP requests based on user roles and public paths.
 */
@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    private static final Set<String> PUBLIC_PATH = Set.of("/login", "/registration", "/logout",
            "/css/", "/js/", "/changeLanguage");
    private static final Map<String, Set<String>> ROLE_URL_MAP = Map.of(
            "ADMIN", Set.of("/admin", "/admin/*"),
            "USER", Set.of("/view_available_events", "/view_available_tickets", "/user_cart", "/purchase")
    );

    /**
     * Filters incoming requests to check user authorization.
     * If the user is not logged in, they are redirected to the login page.
     * If the user is logged in but does not have the proper role,
     * a forbidden status is returned.
     *
     * @param servletRequest the request object
     * @param servletResponse the response object
     * @param filterChain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        var uri = request.getRequestURI();
        var user = (User) request.getSession().getAttribute("user");
        var role = (String) request.getSession().getAttribute("role");

        Locale locale = (Locale) request.getSession().getAttribute("locale");
        if (locale != null) {
            request.setAttribute("locale", locale);
        }

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