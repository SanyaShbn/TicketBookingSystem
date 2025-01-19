package com.example.ticketbookingsystem.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Utility class for Hibernate {@link SessionFactory} management.
 * Provides methods for initializing and shutting down the {@link SessionFactory}.
 */
@Slf4j
public class HibernateUtil {
    @Getter
    private static final SessionFactory sessionFactory;

    private HibernateUtil() {}

    static {
        try{
            Configuration configuration = new Configuration().configure();
            sessionFactory = configuration.buildSessionFactory();
        } catch (HibernateException e) {
            log.error("Initial SessionFactory creation failed: {}", e.getMessage(), e);
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed.");
        }
    }

    /**
     * Shuts down the {@link SessionFactory}, closing all resources.
     */
    public static void shutdown(){
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

}

