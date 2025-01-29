//package com.example.ticketbookingsystem.servlet.authorization_servlets;
//
//import com.example.ticketbookingsystem.dto.UserDto;
//import com.example.ticketbookingsystem.entity.Role;
//import com.example.ticketbookingsystem.entity.User;
//import com.example.ticketbookingsystem.service.UserService;
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
// * Servlet for managing user authentication.
// */
//@WebServlet("/login")
//public class LoginServlet extends HttpServlet {
//    private final UserService userService = UserService.getInstance();
//
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        req.getRequestDispatcher(JspFilesResolver.getPath("login")).forward(req, resp);
//    }
//
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        userService.login(req.getParameter("email"), req.getParameter("password"))
//                .ifPresentOrElse(userDto -> {
//                            try {
//                                onLoginSuccess(userDto, req, resp);
//                            } catch (IOException e) {
//                                throw new RuntimeException(e);
//                            }
//                        },
//                        () -> {
//                            try {
//                                onLoginFail(req, resp);
//                            } catch (IOException | ServletException e) {
//                                throw new RuntimeException(e);
//                            }
//                        });
//    }
//
//    private void onLoginSuccess(UserDto userDto, HttpServletRequest req, HttpServletResponse resp) throws IOException {
//        Optional<User> optionalUser = userService.findByEmail(userDto.getEmail());
//
//        optionalUser.ifPresentOrElse(user -> {
//            req.getSession().setAttribute("user", user);
//
//            String userRole = user.getRole().name();
//
//            req.getSession().setAttribute("role", userRole);
//            try {
//                if(userRole.equals(Role.ADMIN.name())){
//                    resp.sendRedirect("/admin");
//                }
//                else{
//                    resp.sendRedirect("/view_available_events");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }, () -> {
//            try {
//                resp.sendRedirect("/login?error=userNotFound");
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    private void onLoginFail(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
//        ValidationResult validationResult = new ValidationResult();
//        validationResult.add(Error.of("login.fail",
//                "Неправильный логин или пароль"));
//        req.setAttribute("errors", validationResult.getErrors());
//        doGet(req, resp);
//    }
//}
