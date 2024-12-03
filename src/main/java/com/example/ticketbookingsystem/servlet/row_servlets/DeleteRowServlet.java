package com.example.ticketbookingsystem.servlet.row_servlets;

import com.example.ticketbookingsystem.service.RowService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete-row")
public class DeleteRowServlet extends HttpServlet {
    private final RowService rowService = RowService.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");

        rowService.deleteRow(Long.parseLong(id));

        response.sendRedirect(request.getContextPath() + "/rows?arenaId=" +
                request.getParameter("arenaId")+ "&sectorId=" + request.getParameter("sectorId"));
    }
}