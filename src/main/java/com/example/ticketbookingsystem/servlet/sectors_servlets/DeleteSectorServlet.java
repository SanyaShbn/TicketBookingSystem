//package com.example.ticketbookingsystem.servlet.sectors_servlets;
//
//import com.example.ticketbookingsystem.exception.DaoCrudException;
//import com.example.ticketbookingsystem.service.SectorService;
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
// * Servlet for managing requests to delete a sector.
// */
//@WebServlet("/admin/delete-sector")
//public class DeleteSectorServlet extends HttpServlet {
//    private final SectorService sectorService = SectorService.getInstance();
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
//        try {
//            String id = request.getParameter("id");
//
//            sectorService.deleteSector(Long.parseLong(id));
//
//            response.sendRedirect(request.getContextPath() + "/admin/sectors?arenaId="
//                                  + request.getParameter("arenaId"));
//        }catch (DaoCrudException e){
//            ValidationResult validationResult = new ValidationResult();
//            validationResult.add(Error.of("delete.row.fail",
//                    "Ошибка! Данные о выбранном секторе арены нельзя удалить. На него уже добавлены билеты " +
//                    "в разделе 'Предстоящие спортивные события'"));
//            request.setAttribute("errors", validationResult.getErrors());
//            request.getRequestDispatcher(JspFilesResolver.getPath("/error-jsp/error-page"))
//                    .forward(request, response);
//        }
//    }
//}
