package com.example.ticketbookingsystem.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Locale;

/**
 * LocaleFilter is a servlet filter that handles the locale setting for the application
 * based on user preferences, session attributes, and cookies.
 */
@WebFilter("/*")
public class LocaleFilter implements Filter {
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7; // 7 дней

    /**
     * Filters incoming requests to handle locale settings.
     * If a language parameter is provided, it updates the session and cookie with the new locale.
     * Otherwise, it sets the locale from the session or cookie.
     *
     * @param request the request object
     * @param response the response object
     * @param chain the filter chain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();

        String language = httpRequest.getParameter("lang");
        if (language != null) {
            handleLanguageChange(httpRequest, httpResponse, session, language);
            return;
        }

        Locale locale = getLocaleFromSessionOrCookie(httpRequest, session);
        if (locale != null) {
            session.setAttribute("locale", locale);
        }

        chain.doFilter(request, response);
    }

    private void handleLanguageChange(HttpServletRequest request, HttpServletResponse response, HttpSession session,
                                      String language) throws IOException {
        Locale locale = new Locale(language);
        session.setAttribute("locale", locale);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("locale".equals(cookie.getName())) {
                    cookie.setValue(locale.toString());
                    cookie.setPath("/");
                    cookie.setMaxAge(COOKIE_MAX_AGE);
                    response.addCookie(cookie);
                }
            }
        }

        Cookie localeCookie = new Cookie("locale", locale.toString());
        localeCookie.setPath("/");
        localeCookie.setMaxAge(COOKIE_MAX_AGE);
        response.addCookie(localeCookie);

        response.sendRedirect(request.getHeader("referer"));
    }

    private Locale getLocaleFromSessionOrCookie(HttpServletRequest request, HttpSession session) {
        Locale locale = (Locale) session.getAttribute("locale");
        if (locale == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("locale".equals(cookie.getName())) {
                        locale = new Locale(cookie.getValue());
                        break;
                    }
                }
            }
        }
        return locale;
    }
}