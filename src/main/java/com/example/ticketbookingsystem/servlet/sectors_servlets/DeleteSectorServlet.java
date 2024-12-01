package com.example.ticketbookingsystem.servlet.sectors_servlets;

import com.example.ticketbookingsystem.service.SectorService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete-sector")
public class DeleteSectorServlet extends HttpServlet {
    private final SectorService sectorService = SectorService.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");

        sectorService.deleteSector(Long.parseLong(id));

        response.sendRedirect(request.getContextPath() + "/sectors?arenaId="
                + request.getParameter("arenaId"));
    }
}
