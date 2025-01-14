package com.example.ticketbookingsystem.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Load configuration and build session factory
            sessionFactory = new Configuration().configure().buildSessionFactory();
        } catch (Exception e) {
            log.error("Initial SessionFactory creation failed: {}", e.getMessage(), e);
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed.");
        }
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}

