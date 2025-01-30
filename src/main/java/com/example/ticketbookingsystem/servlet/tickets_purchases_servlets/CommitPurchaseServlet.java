//package com.example.ticketbookingsystem.servlet.tickets_purchases_servlets;
//
//import com.example.ticketbookingsystem.entity.User;
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.service.PurchasedTicketsService;
//import com.example.ticketbookingsystem.service.UserCartService;
//import com.example.ticketbookingsystem.utils.JspFilesResolver;
//import com.example.ticketbookingsystem.validator.Error;
//import com.example.ticketbookingsystem.validator.ValidationResult;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.List;
//
///**
// * Servlet for managing ticket purchase commitment.
// */
//@WebServlet("/purchase")
//public class CommitPurchaseServlet extends HttpServlet {
//    private final UserCartService userCartService = UserCartService.getInstance();
//    private final PurchasedTicketsService purchasedTicketsService = PurchasedTicketsService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.getRequestDispatcher(JspFilesResolver.getPath(
//                "/tickets-purchases-jsp/purchase")).forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        User user = (User) request.getSession().getAttribute("user");
//        List<Long> ticketIds = userCartService.getTicketIds(user.getId());
//
//        boolean sufficientFunds = simulatePayment();
//
//        if (sufficientFunds) {
//            try {
//                purchasedTicketsService.savePurchasedTickets(ticketIds, user.getId());
//                response.sendRedirect(request.getContextPath() + "/purchasedTickets?userId=" + user.getId());
//            } catch (DaoCrudException e) {
//                handlePurchaseError(request, response,
//                        "Ошибка! Покупка выбранных билетов уже была осуществлена");
//            }
//        } else {
//            handlePurchaseError(request, response, "Ошибка! Недостаточно средств на выбранной карте");
//        }
//    }
//
//    private boolean simulatePayment() {
//        return Math.random() > 0.2; //Вероятность симуляции недостатка средств на карте и ошибки транзакции (20%)
//    }
//
//    private void handlePurchaseError(HttpServletRequest request, HttpServletResponse response, String errorMessage)
//            throws ServletException, IOException {
//        ValidationResult validationResult = new ValidationResult();
//        validationResult.add(Error.of("purchase.error", errorMessage));
//        request.setAttribute("errors", validationResult.getErrors());
//        doGet(request, response);
//    }
//}
