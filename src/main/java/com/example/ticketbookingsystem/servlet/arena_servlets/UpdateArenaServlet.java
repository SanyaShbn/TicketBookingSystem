package com.example.ticketbookingsystem.servlet.arena_servlets;

import com.example.ticketbookingsystem.dto.ArenaDto;
import com.example.ticketbookingsystem.entity.Arena;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.service.ArenaService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/update-arena")
public class UpdateArenaServlet extends HttpServlet {
    private final ArenaService arenaService = ArenaService.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        Optional<Arena> arena = arenaService.findById(Long.parseLong(id));
        request.setAttribute("arena", arena.get());
        request.getRequestDispatcher(JspFilesResolver.getPath("update-arena")).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String id = request.getParameter("id");
            String name = request.getParameter("name");
            String city = request.getParameter("city");
            int capacity = Integer.parseInt(request.getParameter("capacity"));

            ArenaDto arenaDto = ArenaDto.builder()
                    .name(name)
                    .city(city)
                    .capacity(capacity).build();

            arenaService.updateArena(Long.parseLong(id), arenaDto);

            response.sendRedirect(request.getContextPath() + "/arenas");
        }catch (NumberFormatException e){
            ValidationResult numberFormatValidationResult = new ValidationResult();
            numberFormatValidationResult.add(Error.of("invalid.number.format",
                    "Проверьте корректность ввода данных значения вместимости " +
                            "(допускается вводить только целые числа в диапазоне от 1 до 22000)!"));
            request.setAttribute("errors", numberFormatValidationResult.getErrors());
            doGet(request, response);
        }
        catch (ValidationException e){
            request.setAttribute("errors", e.getErrors());
            doGet(request, response);
        }
    }
}
