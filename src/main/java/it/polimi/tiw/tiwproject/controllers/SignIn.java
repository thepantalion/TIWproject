package it.polimi.tiw.tiwproject.controllers;

import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.dao.UserDAO;
import it.polimi.tiw.tiwproject.utilities.ConnectionHandler;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.rmi.server.ServerCloneException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/SignIn")
public class SignIn extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public SignIn() { super(); }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username;
        String password;

        try {
            username = StringEscapeUtils.escapeJava(request.getParameter("username"));
            password = StringEscapeUtils.escapeJava(request.getParameter("password"));

            if(username == null || password == null || username.isEmpty() || password.isEmpty()) {
                throw new Exception("Missing or empty credential value");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
            return;
        }
        
        UserDAO userDAO = new UserDAO(connection);
        User user;
        
        try{
            user = userDAO.checkCredentials(username, password);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "It was not possible to retrieve the specified user data.");
            return;
        }

        String path;
        if(user == null) {
            ServletContext servletContext = getServletContext();
            final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
            webContext.setVariable("signInErrorMessage", "Incorrect username or password");
            path = "/index.html";
            templateEngine.process(path, webContext, response.getWriter());
        } else {
            request.getSession().setAttribute("user", user);
            response.sendRedirect(getServletContext().getContextPath() + "/Home");
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}
