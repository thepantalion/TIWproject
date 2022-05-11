package it.polimi.tiw.tiwproject.controllers;

import it.polimi.tiw.tiwproject.beans.Meeting;
import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.dao.MeetingDAO;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@WebServlet("/NewMeeting")
public class NewMeeting extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public NewMeeting() {
        super();
    }

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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        if (session.isNew() || session.getAttribute("user") == null) {
            String loginpath = getServletContext().getContextPath() + "/index.html";
            response.sendRedirect(loginpath);
            return;
        }

        String title;
        int duration;
        Time time;
        Date date;
        int numberOfParticipants;

        User user = (User) request.getSession().getAttribute("user");

        try {
            title = StringEscapeUtils.escapeJava(request.getParameter("title"));
            duration = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("duration")));
            time = new Time(new SimpleDateFormat("HH:mm").parse(StringEscapeUtils.escapeJava(request.getParameter("time"))).getTime());
            date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("date"));
            numberOfParticipants = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("numberOfParticipants")));

            if (title == null || duration == 0 || date == null || numberOfParticipants == 0 || title.isEmpty()) throw new Exception();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }

        if (duration <= 0 || numberOfParticipants < 1){
            sendError("The numbers entered are not correct.", request, response);
            return;
        }

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar todayTime = Calendar.getInstance();
        today.set(Calendar.YEAR, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if ((date.equals(today.getTime()) && time.before(Calendar.getInstance().getTime()))
                || date.before(Calendar.getInstance().getTime())){
            sendError("You cannot enter a prior date to today.", request, response);
            return;
        }

        Meeting tempMeeting = new Meeting(user.getUsername(), title, date, time, duration, numberOfParticipants);
        request.getSession().setAttribute("tempMeeting", tempMeeting);
        request.getSession().setAttribute("counter", 0);
        request.getSession().setAttribute("userList", new ArrayList<User>());

        ArrayList<User> allUsers;
        try {
            allUsers = new UserDAO(connection).getAllUsers(user);
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was a problem with the database :(");
            return;
        }

        String path = "/WEB-INF/registry.html";
        ServletContext servletContext = getServletContext();
        final WebContext webContext = new WebContext(request, response, servletContext, request.getLocale());
        webContext.setVariable("allUsers", allUsers);
        webContext.setVariable("tempMeeting", tempMeeting);
        templateEngine.process(path, webContext, response.getWriter());
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    private void sendError(String error, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().setAttribute("errorMessage", error);
        response.sendRedirect(getServletContext().getContextPath() + "/Home");
    }
}
