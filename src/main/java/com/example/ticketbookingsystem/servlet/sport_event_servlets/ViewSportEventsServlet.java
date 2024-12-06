//package com.example.ticketbookingsystem.servlet.sport_event_servlets;
//
//import com.example.ticketbookingsystem.entity.Arena;
//import com.example.ticketbookingsystem.service.ArenaService;
//import com.example.ticketbookingsystem.service.SportEventService;
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
//@WebServlet("/sport_events")
//public class ViewSportEventsServlet extends HttpServlet {
//    private final SportEventService sportEventService = SportEventService.getInstance();
//    private final ArenaService arenaService = ArenaService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html");
//        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
//
//        int defaultLimit = 8;
//        int defaultPage = 1;
//        req.setAttribute("limit", defaultLimit);
//        req.setAttribute("page", defaultPage);
//
//        req.setAttribute("sport_events", sportEventService.findAll());
//
//        List<Arena> arenas = arenaService.findAll();
//        req.setAttribute("arenas", arenas);
//
//        req.getRequestDispatcher(JspFilesResolver.getPath("/sport-events-jsp/sport_events")).forward(req, resp);
//    }
//}
