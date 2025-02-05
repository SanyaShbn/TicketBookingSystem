//package com.example.ticketbookingsystem.config;
//
//import jakarta.servlet.ServletContext;
//import jakarta.servlet.ServletRegistration;
//import org.springframework.web.WebApplicationInitializer;
//import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
//import org.springframework.web.servlet.DispatcherServlet;
//
///**
// * Initializes the web application for the Ticket Booking System.
// * Implements WebApplicationInitializer to configure the ServletContext
// * programmatically and register the DispatcherServlet.
// */
//public class WebAppInitializer implements WebApplicationInitializer {
//
//    /**
//     * Configures the ServletContext with necessary configurations and mappings.
//     *
//     * @param servletContext The ServletContext to configure.
//     */
//    @Override
//    public void onStartup(ServletContext servletContext) {
//
//        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
//        context.register(ApplicationConfiguration.class);
//        context.register(SecurityConfiguration.class);
//        context.register(WebMvcConfig.class);
//
//        DispatcherServlet servlet = new DispatcherServlet(context);
//        ServletRegistration.Dynamic registration = servletContext.addServlet("dispatcher", servlet);
//        registration.setLoadOnStartup(1);
//        registration.addMapping("/");
//    }
//}