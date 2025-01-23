//package com.example.ticketbookingsystem.servlet.arena_servlets;
//
//import com.example.ticketbookingsystem.dto.ArenaDto;
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.exception.ValidationException;
//import com.example.ticketbookingsystem.service.ArenaService;
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
//
///**
// * Servlet for managing requests to create an arena.
// */
//@WebServlet("/admin/create-arena")
//public class CreateArenaServlet extends HttpServlet {
//    private final ArenaService arenaService = ArenaService.getInstance();
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/create-arena")).forward(request, response);
//    }
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
//        try{
//            String name = req.getParameter("name");
//            String city = req.getParameter("city");;
//            int capacity = Integer.parseInt(req.getParameter("capacity"));
//
//            ArenaDto arenaDto = ArenaDto.builder()
//                    .name(name)
//                    .city(city)
//                    .capacity(capacity).build();
//
//            arenaService.createArena(arenaDto);
//
//            resp.sendRedirect(req.getContextPath() + "/admin/arenas");
//        }catch (NumberFormatException e) {
//            handleNumberFormatException(req, resp);
//        } catch (DaoCrudException e) {
//            handleCreateArenaException(req, resp, e);
//        } catch (ValidationException e) {
//            handleValidationException(req, resp, e);
//        }
//    }
//    private void handleNumberFormatException(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        ValidationResult validationResult = new ValidationResult();
//        validationResult.add(Error.of("invalid.number.format",
//                "Проверьте корректность ввода данных!"));
//        request.setAttribute("errors", validationResult.getErrors());
//        doGet(request, response);
//    }
//
//    private void handleCreateArenaException(HttpServletRequest request, HttpServletResponse response,
//                                                   DaoCrudException e)
//            throws ServletException, IOException {
//        ValidationResult sqlExceptionResult = new ValidationResult();
//        specifySQLException(e.getMessage(), sqlExceptionResult);
//        request.setAttribute("errors", sqlExceptionResult.getErrors());
//        doGet(request, response);
//    }
//    private void handleValidationException(HttpServletRequest request, HttpServletResponse response,
//                                           ValidationException e)
//            throws ServletException, IOException {
//        request.setAttribute("errors", e.getErrors());
//        doGet(request, response);
//    }
//    private void specifySQLException(String errorMessage, ValidationResult sqlExceptionResult) {
//        if (errorMessage != null) {
//            if (errorMessage.contains("unique_name")) {
//                sqlExceptionResult.add(Error.of("create.arena.fail",
//                        "Имя арены должно быть уникальным. Проверьте корректность ввода данных"));
//            } else {
//                sqlExceptionResult.add(Error.of("create.row.fail", errorMessage));
//            }
//        } else {
//            sqlExceptionResult.add(Error.of("create.arena.fail", "Unknown sql exception"));
//        }
//    }
//}
