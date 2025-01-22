package com.example.ticketbookingsystem.spring;

import com.example.ticketbookingsystem.config.ApplicationConfiguration;
import com.example.ticketbookingsystem.entity.Arena;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringRunner {
    public static void main(String[] args) {
        var context = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        var arena = context.getBean(Arena.class);

        System.out.println(arena);

        context.close();
    }
}
