package com.example.ticketbookingsystem.servlet.arena_servlets;

import com.example.ticketbookingsystem.dto.ArenaDto;
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

@WebServlet("/create-arena")
public class CreateArenaServlet extends HttpServlet {
    private final ArenaService arenaService = ArenaService.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/create-arena")).forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try{
            String name = req.getParameter("name");
            String city = req.getParameter("city");;
            int capacity = Integer.parseInt(req.getParameter("capacity"));

            ArenaDto arenaDto = ArenaDto.builder()
                    .name(name)
                    .city(city)
                    .capacity(capacity).build();

            arenaService.createArena(arenaDto);

            resp.sendRedirect(req.getContextPath() + "/arenas");
        }catch (NumberFormatException e){
            ValidationResult numberFormatValidationResult = new ValidationResult();
            numberFormatValidationResult.add(Error.of("invalid.number.format",
                    "Проверьте корректность ввода данных значения вместимости " +
                            "(допускается вводить только целые числа в диапазоне от 1 до 22000)!"));
            req.setAttribute("errors", numberFormatValidationResult.getErrors());
            doGet(req, resp);
        }
        catch (ValidationException e){
            req.setAttribute("errors", e.getErrors());
            doGet(req, resp);
        }
    }
}
