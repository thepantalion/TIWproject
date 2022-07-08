package it.polimi.tiw.tiwproject.controllers;

import it.polimi.tiw.tiwproject.beans.Meeting;
import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.dao.MeetingDAO;
import it.polimi.tiw.tiwproject.utilities.ConnectionHandler;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/Home")
public class GoToHome extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public GoToHome() { super(); }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String errorMessage = "";

        if (session.getAttribute("tempMeeting") != null) session.removeAttribute("tempMeeting");
        if (session.getAttribute("counter") != null) session.removeAttribute("counter");
        if (session.getAttribute("userMap") != null) session.removeAttribute("userMap");
        if (session.getAttribute("errorMessage") != null) {
            errorMessage = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
        }

        User user = (User) session.getAttribute("user");
        MeetingDAO meetingDAO = new MeetingDAO(connection);
        ArrayList<Meeting> meetingsCreated;
        ArrayList<Meeting> meetingsInvited;

        try {
            meetingsCreated = meetingDAO.meetingsCreated(user);
            meetingsInvited = meetingDAO.meetingsInvited(user);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to recover meeting");
            return;
        }

        String path = "/WEB-INF/home.html";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("user", user);
        webContext.setVariable("meetingsCreated", meetingsCreated);
        webContext.setVariable("meetingsInvited", meetingsInvited);
        if(!errorMessage.isEmpty()) webContext.setVariable("meetingFormError", errorMessage);
        templateEngine.process(path, webContext, response.getWriter());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doGet(request, response);
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
