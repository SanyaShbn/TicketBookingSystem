package com.example.ticketbookingsystem.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * The ApplicationConfiguration class provides the main configuration
 * for the Ticket Booking System application.
 * It scans the specified base packages for Spring-managed components
 * and enables JPA repositories in the specified package.
 */
@Configuration
@ComponentScan(basePackages = "com.example.ticketbookingsystem")
@EnableJpaRepositories("com.example.ticketbookingsystem.repository")
public class ApplicationConfiguration {

}