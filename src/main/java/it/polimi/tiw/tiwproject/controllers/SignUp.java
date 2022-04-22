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
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
    private Connection connection;
    private TemplateEngine templateEngine;

    public SignUp() {
        super();
    }

    @Override
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
        String email;
        String password;
        String passwordRepeated;

        email = StringEscapeUtils.escapeJava(request.getParameter("email"));
        username = StringEscapeUtils.escapeJava(request.getParameter("username"));
        password = StringEscapeUtils.escapeJava(request.getParameter("password"));
        passwordRepeated = StringEscapeUtils.escapeJava(request.getParameter("password"));

        if (email == null || username == null || password == null || passwordRepeated == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }

        if (email.isEmpty() || username.isEmpty() || password.isEmpty() || passwordRepeated.isEmpty()) {
            sendError("One or more fields are empty", request, response);
            return;
        }

        if (!password.equals(passwordRepeated)){
            sendError("The two passwords do not match", request, response);
            return;
        }

        //create User in db
        UserDAO userDAO = new UserDAO(connection);
        try {
            userDAO.createUser(email, username, password);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to add user :(");
            return;
        }

        // return the user to the right view
        String ctxpath = getServletContext().getContextPath();
        String path = ctxpath + "/index.html";
        response.sendRedirect(path);

    }

    private void sendError(String error, HttpServletRequest request, HttpServletResponse response){
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("errorMessage", error);
        String path = "/index.html";

        try {
            templateEngine.process(path, webContext, response.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
