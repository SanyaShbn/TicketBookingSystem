package com.example.ticketbookingsystem.servlet.authorization_servlets;

import com.example.ticketbookingsystem.dto.UserDto;
import com.example.ticketbookingsystem.service.UserService;
import com.example.ticketbookingsystem.utils.JspFilesResolver;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserService userService = UserService.getInstance();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher(JspFilesResolver.getPath("login")).forward(req, resp);
    }


    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        userService.login(req.getParameter("email"), req.getParameter("password"))
                .ifPresentOrElse(userDto -> {
                            try {
                                onLoginSuccess(userDto, req, resp);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        () -> {
                            try {
                                onLoginFail(req, resp);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
    }


    private void onLoginSuccess(UserDto userDto, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().setAttribute("user", userDto);
        resp.sendRedirect("/index");
    }

    private void onLoginFail(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect("/login?error&email=" + req.getParameter("email"));
    }
}
