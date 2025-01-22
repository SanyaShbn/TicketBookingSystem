package com.example.ticketbookingsystem.config;

import com.example.ticketbookingsystem.entity.Arena;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.example.ticketbookingsystem")
public class ApplicationConfiguration {

    @Bean
    public Arena arena(){
        return new Arena(1L, "Arena", "City", 50, 100);
    }
}