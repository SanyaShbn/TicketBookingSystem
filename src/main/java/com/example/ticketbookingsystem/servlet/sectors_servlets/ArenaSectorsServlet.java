//package com.example.ticketbookingsystem.servlet.sectors_servlets;
//
//import com.example.ticketbookingsystem.dto.sector_dto.SectorFilter;
//import com.example.ticketbookingsystem.entity.Sector;
//import com.example.ticketbookingsystem.service.SectorService;
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
// * Servlet for managing requests to get the list of available sectors.
// */
//@WebServlet("/admin/sectors")
//public class ArenaSectorsServlet extends HttpServlet {
//    private final SectorService sectorService = SectorService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html");
//        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
//
//        setRequestAttributes(req);
//
//        req.getRequestDispatcher(JspFilesResolver.getPath("/sectors-jsp/sectors")).forward(req, resp);
//    }
//
//    private void setRequestAttributes(HttpServletRequest req) {
//        SectorFilter sectorFilter = buildSectorFilter(req);
//        Long arenaId = Long.valueOf(req.getParameter("arenaId"));
//        List<Sector> sectorList = sectorService.findAll(sectorFilter, arenaId);
//
//        req.setAttribute("sectors", sectorList);
//
//        req.setAttribute("limit", sectorFilter.limit());
//        int currentPage = req.getParameter("page") != null
//                ? Integer.parseInt(req.getParameter("page")) : 1;
//        req.setAttribute("page", currentPage);
//    }
//    private SectorFilter buildSectorFilter(HttpServletRequest req) {
//        String nameSortOrder = req.getParameter("nameSortOrder") != null
//                ? req.getParameter("nameSortOrder") : "";
//        String maxRowsNumbSortOrder = req.getParameter("maxRowsNumbSortOrder") != null
//                ? req.getParameter("maxRowsNumbSortOrder") : "";
//        String maxSeatsNumbSortOrder = req.getParameter("maxSeatsNumbSortOrder") != null
//                ? req.getParameter("maxSeatsNumbSortOrder") : "";
//
//        int limit = 8;
//        int offset = req.getParameter("page") != null
//                ? (Integer.parseInt(req.getParameter("page")) - 1) * limit : 0;
//
//        return new SectorFilter(nameSortOrder, maxRowsNumbSortOrder, maxSeatsNumbSortOrder, limit, offset);
//    }
//}
