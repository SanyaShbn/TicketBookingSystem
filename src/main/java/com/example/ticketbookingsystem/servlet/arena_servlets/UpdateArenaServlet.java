//package com.example.ticketbookingsystem.servlet.arena_servlets;
//
//import com.example.ticketbookingsystem.dto.ArenaDto;
//import com.example.ticketbookingsystem.entity.Arena;
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
//import java.util.Optional;
//
///**
// * Servlet for managing requests to update an arena.
// */
//@WebServlet("/admin/update-arena")
//public class UpdateArenaServlet extends HttpServlet {
//    private final ArenaService arenaService = ArenaService.getInstance();
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String id = request.getParameter("id");
//        Optional<Arena> arena = arenaService.findById(Long.parseLong(id));
//        request.setAttribute("arena", arena.get());
//        request.getRequestDispatcher(JspFilesResolver.getPath("/arena-jsp/update-arena")).forward(request, response);
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        try {
//            String id = request.getParameter("id");
//            String name = request.getParameter("name");
//            String city = request.getParameter("city");
//            int capacity = Integer.parseInt(request.getParameter("capacity"));
//
//            ArenaDto arenaDto = ArenaDto.builder()
//                    .name(name)
//                    .city(city)
//                    .capacity(capacity).build();
//
//            arenaService.updateArena(Long.parseLong(id), arenaDto);
//
//            response.sendRedirect(request.getContextPath() + "/admin/arenas");
//        }catch (NumberFormatException e) {
//            handleNumberFormatException(request, response);
//        } catch (DaoCrudException e) {
//            handleUpdateArenaException(request, response, e);
//        } catch (ValidationException e) {
//            handleValidationException(request, response, e);
//        }
//    }
//
//    private void handleNumberFormatException(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        ValidationResult validationResult = new ValidationResult();
//        validationResult.add(Error.of("invalid.number.format",
//                "Проверьте корректность ввода данных!"));
//        request.setAttribute("errors", validationResult.getErrors());
//        doGet(request, response);
//    }
//
//    private void handleUpdateArenaException(HttpServletRequest request, HttpServletResponse response,
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
//            switch (getErrorType(errorMessage)) {
//                case "ERROR_CHECK_ARENA_NAME" -> sqlExceptionResult.add(Error.of("create.arena.fail",
//                        "Ошибка! Имя арены должно быть уникальным. Проверьте корректность ввода данных"));
//                case "ERROR_CHECK_CAPACITY" -> sqlExceptionResult.add(Error.of("create.arena.fail",
//                        "Ошибка! Общее максимально возможное количество мест в секторах для " +
//                        "заданной арены превышает ее вместимость. Проверьте корреткность ввода данных"));
//                default -> sqlExceptionResult.add(Error.of("create.row.fail", errorMessage));
//            }
//        } else {
//            sqlExceptionResult.add(Error.of("create.arena.fail", "Unknown sql exception"));
//        }
//    }
//
//    private String getErrorType(String errorMessage) {
//        if (errorMessage.contains("unique_name")) {
//            return "ERROR_CHECK_ARENA_NAME";
//        } else if (errorMessage.contains("ERROR_CHECK_CAPACITY")) {
//            return "ERROR_CHECK_CAPACITY";}
//        else {
//            return "UNKNOWN_ERROR";
//        }
//    }
//}
