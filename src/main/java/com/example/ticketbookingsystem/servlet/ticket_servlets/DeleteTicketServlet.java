//package com.example.ticketbookingsystem.servlet.ticket_servlets;
//
//import com.example.ticketbookingsystem.service.TicketService;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//
///**
// * Servlet for managing requests to delete a ticket.
// */
//@WebServlet("/admin/delete-ticket")
//public class DeleteTicketServlet extends HttpServlet {
//    private final TicketService ticketService = TicketService.getInstance();
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String id = request.getParameter("id");
//        ticketService.deleteTicket(Long.parseLong(id));
//        response.sendRedirect(request.getContextPath() + "/admin/tickets?eventId=" + request.getParameter("eventId"));
//    }
//}
