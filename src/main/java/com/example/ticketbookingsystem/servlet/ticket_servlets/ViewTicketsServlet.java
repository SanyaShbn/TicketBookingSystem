//package com.example.ticketbookingsystem.servlet.ticket_servlets;
//
//import com.example.ticketbookingsystem.dto.TicketFilter;
//import com.example.ticketbookingsystem.entity.Ticket;
//import com.example.ticketbookingsystem.service.TicketService;
//import com.example.ticketbookingsystem.utils.JspFilesResolver;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//
///**
// * Servlet for managing requests to get the list of available tickets.
// */
//@WebServlet("/admin/tickets")
//public class ViewTicketsServlet extends HttpServlet {
//    private final TicketService ticketService = TicketService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html");
//        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
//
//        setRequestAttributes(req);
//
//        req.getRequestDispatcher(JspFilesResolver.getPath("/tickets-jsp/tickets")).forward(req, resp);
//    }
//    private void setRequestAttributes(HttpServletRequest req) {
//        TicketFilter ticketFilter = buildTicketFilter(req);
//        Long eventId = Long.valueOf(req.getParameter("eventId"));
//        List<Ticket> ticketList = ticketService.findAll(ticketFilter, eventId);
//
//        req.setAttribute("tickets", ticketList);
//
//        req.setAttribute("limit", ticketFilter.limit());
//        int currentPage = req.getParameter("page") != null
//                ? Integer.parseInt(req.getParameter("page")) : 1;
//        req.setAttribute("page", currentPage);
//    }
//    private TicketFilter buildTicketFilter(HttpServletRequest req) {
//        String priceSortOrder = req.getParameter("priceSortOrder") != null
//                ? req.getParameter("priceSortOrder") : "";
//
//        int limit = 8;
//        int offset = req.getParameter("page") != null
//                ? (Integer.parseInt(req.getParameter("page")) - 1) * limit : 0;
//
//        return new TicketFilter(priceSortOrder, limit, offset);
//    }
//
//}
