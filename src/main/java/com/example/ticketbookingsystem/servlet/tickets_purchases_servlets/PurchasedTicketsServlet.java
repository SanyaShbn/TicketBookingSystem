package com.example.ticketbookingsystem.servlet.tickets_purchases_servlets;

import com.example.ticketbookingsystem.dto.PurchasedTicketDto;
import com.example.ticketbookingsystem.entity.User;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.service.PurchasedTicketsService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/purchasedTickets")
public class PurchasedTicketsServlet extends HttpServlet {
    private final PurchasedTicketsService purchasedTicketsService = PurchasedTicketsService.getInstance();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = (User) request.getSession().getAttribute("user");

        try {
            List<PurchasedTicketDto> purchasedTickets = purchasedTicketsService.findAllByUserId(user.getId());
            request.setAttribute("purchasedTickets", purchasedTickets);
        } catch (DaoCrudException e) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("get.purchasedTickets.error",
                    "Ошибка! Не удалось получить информацию о приобретенных вами билетах. " +
                    "Обратитесь к администратору сайта"));
            request.setAttribute("errors", validationResult.getErrors());
            doGet(request, response);
        }

        request.getRequestDispatcher(JspFilesResolver.getPath(
                "/tickets-purchases-jsp/purchased-tickets")).forward(request, response);
    }
}
