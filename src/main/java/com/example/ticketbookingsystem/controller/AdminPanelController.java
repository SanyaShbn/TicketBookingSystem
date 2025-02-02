package com.example.ticketbookingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller class for managing the admin panel in the Ticket Booking System application.
 */
@Controller
public class AdminPanelController {

    /**
     * Handles GET requests to the /admin URL.
     *
     * @return The name of the view to be rendered, in this case "admin_panel".
     */
    @GetMapping("/admin")
    public String adminPanelPage(){
        return "admin_panel";
    }
}
