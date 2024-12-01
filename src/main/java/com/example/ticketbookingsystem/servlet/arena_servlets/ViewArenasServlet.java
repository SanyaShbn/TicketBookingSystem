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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@WebServlet("/arenas")
public class ViewArenasServlet extends HttpServlet {
    private final ArenaService arenaService = ArenaService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        if (id != null) {
            Optional<Arena> arena = arenaService.findById(Long.parseLong(id));
            if (arena.isPresent()) {
                req.setAttribute("arena", arena.get());
                req.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/update-arena")).forward(req, resp);
                return;
            }
        }

        List<Arena> arenas = arenaService.findAll();
        req.setAttribute("arenas", arenas);
        req.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/arenas")).forward(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        req.setAttribute("arenas", arenaService.findAll());
        req.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/arenas")).forward(req, resp);
    }
}
