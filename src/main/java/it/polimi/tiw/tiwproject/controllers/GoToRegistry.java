package it.polimi.tiw.tiwproject.controllers;

import it.polimi.tiw.tiwproject.beans.Meeting;
import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.dao.UserDAO;
import it.polimi.tiw.tiwproject.utilities.ConnectionHandler;
import it.polimi.tiw.tiwproject.utilities.Pair;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.enterprise.context.Dependent;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

@WebServlet("/Registry")
public class GoToRegistry extends HttpServlet {
    private Connection connection;
    private TemplateEngine templateEngine;

    public GoToRegistry() { super(); }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        HttpSession session = request.getSession();
        String errorMessage = "";

        if (session.isNew() || session.getAttribute("user") == null) {
            String loginPath = getServletContext().getContextPath() + "/index.html";
            response.sendRedirect(loginPath);
            return;
        }

        if(session.getAttribute("tempMeeting") == null || session.getAttribute("counter") == null || session.getAttribute("userMap") == null){
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing some parameters. Are you a maleficent client? /:|");
            return;
        }

        if (session.getAttribute("errorMessage") != null) {
            errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
        }

        UserDAO userDAO = new UserDAO(connection);
        HashMap<String, Pair<User, Boolean>> userMap = (HashMap<String, Pair<User, Boolean>>) session.getAttribute("userMap");
        Meeting tempMeeting = (Meeting) session.getAttribute("tempMeeting");

        try {
            userMap = userDAO.addNewUsers(user, userMap);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was a problem with the database. :(");
            return;
        }

        String path = "/WEB-INF/registry.html";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("userMap", userMap);
        webContext.setVariable("tempMeeting", tempMeeting);
        webContext.setVariable("meetingFormError", errorMessage);
        templateEngine.process(path, webContext, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
