//package com.example.ticketbookingsystem.servlet.sport_event_servlets;
//
//import com.example.ticketbookingsystem.dto.sport_event_dto.SportEventFilter;
//import com.example.ticketbookingsystem.entity.Arena;
//import com.example.ticketbookingsystem.entity.SportEvent;
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
//import java.time.LocalDateTime;
//import java.util.List;
//
///**
// * Servlet for managing requests to get the list of available sporting events.
// */
//@WebServlet("/admin/sport_events")
//public class ViewSportEventsServlet extends HttpServlet {
//    private final SportEventService sportEventService = SportEventService.getInstance();
//    private final ArenaService arenaService = ArenaService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html");
//        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
//
//        SportEventFilter sportEventFilter = buildSportEventFilter(req);
//        List<SportEvent> sportEventList = sportEventService.findAll(sportEventFilter);
//
//        req.setAttribute("sport_events", sportEventList);
//
//        List<Arena> arenas = arenaService.findAll();
//        req.setAttribute("arenas", arenas);
//
//        req.setAttribute("limit", sportEventFilter.limit());
//        int currentPage = req.getParameter("page") != null
//                ? Integer.parseInt(req.getParameter("page")) : 1;
//        req.setAttribute("page", currentPage);
//
//        req.getRequestDispatcher(JspFilesResolver.getPath("/sport-events-jsp/sport_events")).forward(req, resp);
//    }
//
//    private SportEventFilter buildSportEventFilter(HttpServletRequest req) {
//        LocalDateTime startDate = parseLocalDateTime(req.getParameter("startDate"));
//        LocalDateTime endDate = parseLocalDateTime(req.getParameter("endDate"));
//        Long arenaId = parseLong(req.getParameter("arenaId"));
//        String sortOrder = req.getParameter("sortOrder") != null ? req.getParameter("sortOrder") : "";
//
//        int limit = 8;
//        int offset = req.getParameter("page") != null
//                ? (Integer.parseInt(req.getParameter("page")) - 1)* limit : 0;
//
//        return new SportEventFilter(startDate, endDate, arenaId, limit, offset, sortOrder);
//    }
//
//    private LocalDateTime parseLocalDateTime(String param) {
//        return param != null && !param.isEmpty() ? LocalDateTime.parse(param) : null;
//    }
//
//    private Long parseLong(String param) {
//        return param != null && !param.isEmpty() ? Long.valueOf(param) : null;
//    }
//}