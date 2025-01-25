//package com.example.ticketbookingsystem.servlet.row_servlets;
//
//import com.example.ticketbookingsystem.dto.RowFilter;
//import com.example.ticketbookingsystem.entity.Row;
//import com.example.ticketbookingsystem.service.RowService;
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
// * Servlet for managing requests to get the list of available rows.
// */
//@WebServlet("/admin/rows")
//public class ViewRowsServlet extends HttpServlet {
//    private final RowService rowService = RowService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        resp.setContentType("text/html");
//        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
//
//        setRequestAttributes(req);
//        req.getRequestDispatcher(JspFilesResolver.getPath("/rows-jsp/rows")).forward(req, resp);
//    }
//
//    private void setRequestAttributes(HttpServletRequest req) {
//        RowFilter rowFilter = buildRowFilter(req);
//        Long sectorId = Long.valueOf(req.getParameter("sectorId"));
//        List<Row> rowList = rowService.findAll(rowFilter, sectorId);
//
//        req.setAttribute("rows", rowList);
//
//        req.setAttribute("limit", rowFilter.limit());
//        int currentPage = req.getParameter("page") != null
//                ? Integer.parseInt(req.getParameter("page")) : 1;
//        req.setAttribute("page", currentPage);
//    }
//    private RowFilter buildRowFilter(HttpServletRequest req) {
//        String rowNumberOrder = req.getParameter("rowNumberOrder") != null
//                ? req.getParameter("rowNumberOrder") : "";
//        String seatsNumbOrder = req.getParameter("seatsNumbOrder") != null
//                ? req.getParameter("seatsNumbOrder") : "";
//
//        int limit = 8;
//        int offset = req.getParameter("page") != null
//                ? (Integer.parseInt(req.getParameter("page")) - 1) * limit : 0;
//
//        return new RowFilter(rowNumberOrder, seatsNumbOrder, limit, offset);
//    }
//}
