package com.example.ticketbookingsystem.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

@WebFilter("/*")
public class LocaleFilter implements Filter {
    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7; // 7 дней

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

    private void handleLanguageChange(HttpServletRequest request, HttpServletResponse response, HttpSession session, String language)
            throws IOException {
        Locale locale = new Locale(language);
        session.setAttribute("locale", locale);

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("locale".equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        Cookie localeCookie = new Cookie("locale", locale.toString());
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