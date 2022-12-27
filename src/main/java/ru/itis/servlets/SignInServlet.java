package ru.itis.servlets;

import ru.itis.dto.SignInForm;
import ru.itis.dto.UserDto;
import ru.itis.excptions.ValidationException;
import ru.itis.services.SignInService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/sign-in")
public class SignInServlet extends HttpServlet {
    private SignInService signInService;

    @Override
    public void init(ServletConfig config) throws ServletException {
        ServletContext context = config.getServletContext();
        this.signInService = (SignInService) context.getAttribute("signInService");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("sign_in.ftl").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SignInForm form = SignInForm.builder()
                .email(request.getParameter("email"))
                .password(request.getParameter("password"))
                .build();
        UserDto userDto;

        try {
            userDto = signInService.signIn(form);
        } catch (ValidationException e) {
            response.sendRedirect("/sign-in");
            return;
        }

        response.addCookie(new Cookie("email", userDto.getEmail()));
        HttpSession session = request.getSession(true);
        session.setAttribute("user", userDto);
        response.sendRedirect("/profile");
    }
}
