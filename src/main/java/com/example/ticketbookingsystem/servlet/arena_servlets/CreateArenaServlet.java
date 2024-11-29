package com.example.ticketbookingsystem.servlet.arena_servlets;

import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/create-arena")
public class CreateArenaServlet extends HttpServlet {
    private final ArenaService arenaService = ArenaService.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(JspFilesResolver.getPath("create-arena")).forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String city = req.getParameter("city");;
        int capacity = Integer.parseInt(req.getParameter("capacity"));

        Arena arena = new Arena(name, city, capacity);
        arenaService.createArena(arena);

        resp.sendRedirect(req.getContextPath() + "/arenas");
    }
}
