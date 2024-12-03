package com.example.ticketbookingsystem.servlet.row_servlets;

import com.example.ticketbookingsystem.service.RowService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/rows")
public class ViewRowsServlet extends HttpServlet {
    private final RowService rowService = RowService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Long sectorId = Long.valueOf(req.getParameter("sectorId"));

        req.setAttribute("rows", rowService.findAllBySectorId(sectorId));
        req.getRequestDispatcher(JspFilesResolver.getPath("/rows-jsp/rows")).forward(req, resp);
    }
}
