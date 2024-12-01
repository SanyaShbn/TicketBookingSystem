package com.example.ticketbookingsystem.servlet.arena_servlets;

import com.example.ticketbookingsystem.service.ArenaService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete-arena")
public class DeleteArenaServlet extends HttpServlet {
    private final ArenaService arenaService = ArenaService.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");

        arenaService.deleteArena(Long.parseLong(id));

        response.sendRedirect(request.getContextPath() + "/arenas");
    }
}
