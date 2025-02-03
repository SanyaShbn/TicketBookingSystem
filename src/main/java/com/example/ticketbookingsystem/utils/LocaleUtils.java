package com.example.ticketbookingsystem.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

/**
 * Utility class for handling localization cookie.
 */
public class LocaleUtils {
    public static Locale getLocale() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        String lang = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("locale".equals(cookie.getName())) {
                    lang = cookie.getValue();
                    break;
                }
            }
        }

        Locale locale;
        if ("ru".equals(lang)) {
            locale = new Locale("ru", "RU");
        } else {
            locale = Locale.ENGLISH;
        }
        return locale;
    }
}