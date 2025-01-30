//package com.example.ticketbookingsystem.servlet.arena_servlets;
//
//import com.example.ticketbookingsystem.dto.arena_dto.ArenaFilter;
//import com.example.ticketbookingsystem.entity.Arena;
//import com.example.ticketbookingsystem.service.ArenaService;
//import com.example.ticketbookingsystem.utils.JspFilesResolver;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Servlet for managing requests to get the list of available arenas.
// */
//@WebServlet("/admin/arenas")
//public class ViewArenasServlet extends HttpServlet {
//    private final ArenaService arenaService = ArenaService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        String id = req.getParameter("id");
//        if (id != null) {
//            Optional<Arena> arena = arenaService.findById(Long.parseLong(id));
//            if (arena.isPresent()) {
//                req.setAttribute("arena", arena.get());
//                req.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/update-arena")).forward(req, resp);
//                return;
//            }
//        }
//
//        setRequestAttributes(req);
//
//        req.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/arenas")).forward(req, resp);
//    }
//
//    private void setRequestAttributes(HttpServletRequest req) {
//        ArenaFilter arenaFilter = buildArenaFilter(req);
//        List<Arena> arenaList = arenaService.findAll(arenaFilter);
//        List<String> cities = arenaService.findAllArenasCities();
//
//        req.setAttribute("arenas", arenaList);
//        req.setAttribute("cities", cities);
//
//        req.setAttribute("limit", arenaFilter.limit());
//        int currentPage = req.getParameter("page") != null
//                ? Integer.parseInt(req.getParameter("page")) : 1;
//        req.setAttribute("page", currentPage);
//    }
//
//    private ArenaFilter buildArenaFilter(HttpServletRequest req) {
//        String city = req.getParameter("city") != null ? req.getParameter("city") : "";
//        String capacitySortOrder = req.getParameter("capacitySortOrder") != null
//                ? req.getParameter("capacitySortOrder") : "";
//        String seatsNumbSortOrder = req.getParameter("seatsNumbSortOrder") != null
//                ? req.getParameter("seatsNumbSortOrder") : "";
//
//        int limit = 8;
//        int offset = req.getParameter("page") != null
//                ? (Integer.parseInt(req.getParameter("page")) - 1)* limit : 0;
//
//        return new ArenaFilter(city, capacitySortOrder, seatsNumbSortOrder, limit, offset);
//    }
//}
