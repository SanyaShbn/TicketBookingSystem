package com.example.ticketbookingsystem.servlet.sport_event_servlets;

import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.SportEventService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/delete-sport-event")
public class DeleteSportEventServlet extends HttpServlet {
    private final SportEventService sportEventService = SportEventService.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String id = request.getParameter("id");
            sportEventService.deleteSportEvent(Long.parseLong(id));
            response.sendRedirect(request.getContextPath() + "/admin/sport_events");
        }catch (DaoCrudException e){
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.event.error",
                    "Выбранное событие нельзя удалить, на него уже доступна продажа билетов. " +
                    "Если хотите выполнить удаление, необходимо удалить всю информацию о продаваемых билетах"));
            request.setAttribute("errors", validationResult.getErrors());
            request.getRequestDispatcher(JspFilesResolver.getPath("/error-jsp/error-page"))
                    .forward(request, response);
        }
    }
}
