package com.example.ticketbookingsystem.servlet.authorization_servlets;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.entity.Role;
import com.example.ticketbookingsystem.exception.DaoCrudException;
import com.example.ticketbookingsystem.exception.ValidationException;
import com.example.ticketbookingsystem.service.UserService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import com.example.ticketbookingsystem.validator.Error;
import com.example.ticketbookingsystem.validator.ValidationResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    private final UserService userService = UserService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspFilesResolver.getPath("registration")).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var userDto = UserDto.builder()
                .email(req.getParameter("email"))
                .password(req.getParameter("password"))
                .role(Role.USER.name())
                .confirmPassword(req.getParameter("confirm-password"))
                .build();
        try {
            userService.createUser(userDto);
            resp.sendRedirect("/login");
        }catch (DaoCrudException e){
            ValidationResult validationResult = new ValidationResult();
            validationResult.add(Error.of("check.email.uniqueness",
                    "Введенный вами адрес электронной почты уже используется в системе"));
            req.setAttribute("errors", validationResult.getErrors());
            doGet(req, resp);
        }
        catch (ValidationException e){
            req.setAttribute("errors", e.getErrors());
            doGet(req, resp);
        }
    }
}