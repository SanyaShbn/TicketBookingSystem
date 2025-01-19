package com.example.ticketbookingsystem.listener;

import com.example.ticketbookingsystem.utils.HibernateUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * HibernateContextListener is a servlet context listener that manages the lifecycle of
 * the Hibernate `SessionFactory` by initializing and shutting it down along with the servlet context.
 */
@WebListener
public class HibernateContextListener implements ServletContextListener {

    /**
     * Initializes the Hibernate `SessionFactory` when the servlet context is initialized.
     *
     * @param sce the ServletContextEvent containing the servlet context
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Принудительная инициализация `SessionFactory`
        HibernateUtil.getSessionFactory();
    }

    /**
     * Shuts down the Hibernate `SessionFactory` when the servlet context is destroyed.
     *
     * @param sce the ServletContextEvent containing the servlet context
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.shutdown();
    }
}

