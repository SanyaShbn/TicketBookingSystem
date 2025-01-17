package com.example.ticketbookingsystem.servlet.arena_servlets;

import com.example.ticketbookingsystem.exception.CreateUpdateEntityException;
import com.example.ticketbookingsystem.exception.DaoCrudException;
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

@WebServlet("/admin/delete-arena")
public class DeleteArenaServlet extends HttpServlet {
    private final ArenaService arenaService = ArenaService.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String id = request.getParameter("id");

            arenaService.deleteArena(Long.parseLong(id));

            response.sendRedirect(request.getContextPath() + "/admin/arenas");
        }catch (DaoCrudException e){
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.arena.fail",
                    "Ошибка! Данные о выбранной арене нельзя удалить. Для нее уже запланированы события " +
                            "в разделе 'Предстоящие спортивные события'"));
            request.setAttribute("errors", validationResult.getErrors());
            request.getRequestDispatcher(JspFilesResolver.getPath("/error-jsp/error-page"))
                    .forward(request, response);
        }
    }
}
