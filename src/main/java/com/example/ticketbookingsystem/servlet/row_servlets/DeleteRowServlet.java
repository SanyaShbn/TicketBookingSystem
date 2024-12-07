package com.example.ticketbookingsystem.servlet.row_servlets;

import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.RowService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/delete-row")
public class DeleteRowServlet extends HttpServlet {
    private final RowService rowService = RowService.getInstance();
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            String id = request.getParameter("id");

            rowService.deleteRow(Long.parseLong(id));

            response.sendRedirect(request.getContextPath() + "/rows?arenaId=" +
                                  request.getParameter("arenaId") + "&sectorId=" + request.getParameter("sectorId"));
        }catch (DaoCrudException e){
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("delete.row.fail",
                    "Ошибка! Данные о выбранном ряде арены нельзя удалить. На него уже добавлены билеты " +
                    "в разделе 'Предстоящие спортивные события'"));
            request.setAttribute("errors", validationResult.getErrors());
            request.getRequestDispatcher(JspFilesResolver.getPath("/error-jsp/error-page"))
                    .forward(request, response);
        }
    }
}