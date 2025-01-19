package com.example.ticketbookingsystem.utils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
            Properties properties = new Properties();

            properties.setProperty("hibernate.hikari.dataSource.url", System.getenv("DB_URL"));
            properties.setProperty("hibernate.hikari.dataSource.user", System.getenv("DB_USERNAME"));
            properties.setProperty("hibernate.hikari.dataSource.password", System.getenv("DB_PASSWORD"));

            Configuration configuration = new Configuration().configure();
            configuration.setProperties(properties);
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

