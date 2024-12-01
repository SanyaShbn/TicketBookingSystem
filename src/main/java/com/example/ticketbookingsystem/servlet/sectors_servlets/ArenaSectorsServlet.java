package com.example.ticketbookingsystem.servlet.sectors_servlets;

import com.example.ticketbookingsystem.service.SectorService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/sectors")
public class ArenaSectorsServlet extends HttpServlet {
    private final SectorService sectorService = SectorService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Long arenaId = Long.valueOf(req.getParameter("arenaId"));

        req.setAttribute("sectors", sectorService.findAllByArenaId(arenaId));
        req.getRequestDispatcher(JspFilesResolver.getPath("/sectors-jsp/sectors")).forward(req, resp);
    }
}
