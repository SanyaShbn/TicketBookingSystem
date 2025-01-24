package com.example.ticketbookingsystem.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.example.ticketbookingsystem")
@EnableJpaRepositories("com.example.ticketbookingsystem.repository")
public class ApplicationConfiguration {

}