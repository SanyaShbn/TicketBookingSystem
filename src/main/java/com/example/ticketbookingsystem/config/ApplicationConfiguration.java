package com.example.ticketbookingsystem.config;

import com.example.ticketbookingsystem.entity.Arena;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// TODO: настроить MapStruct

@Configuration
@ComponentScan(basePackages = "com.example.ticketbookingsystem")
@EnableJpaRepositories("com.example.ticketbookingsystem.repository")
public class ApplicationConfiguration {

    @Bean
    public Arena arena(){
        return new Arena(1L, "Arena", "City", 50, 100);
    }
}