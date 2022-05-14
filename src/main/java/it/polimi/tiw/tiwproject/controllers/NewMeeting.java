package it.polimi.tiw.tiwproject.controllers;

import it.polimi.tiw.tiwproject.beans.Meeting;
import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.utilities.ConnectionHandler;
import it.polimi.tiw.tiwproject.utilities.Pair;
import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
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
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

@WebServlet("/NewMeeting")
public class NewMeeting extends HttpServlet {
    private Connection connection;
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

        Calendar calendarA = Calendar.getInstance();
        calendarA.setTime(date);
        Calendar calendarB = Calendar.getInstance();
        calendarB.setTime(time);

        calendarA.set(Calendar.HOUR_OF_DAY, calendarB.get(Calendar.HOUR_OF_DAY));
        calendarA.set(Calendar.MINUTE, calendarB.get(Calendar.MINUTE));
        calendarA.set(Calendar.SECOND, calendarB.get(Calendar.SECOND));
        calendarA.set(Calendar.MILLISECOND, calendarB.get(Calendar.MILLISECOND));

        Date result = calendarA.getTime();

        if (result.before(Calendar.getInstance().getTime())){
            sendError("You cannot enter a prior date to today.", request, response);
            return;
        }

        Meeting tempMeeting = new Meeting(user.getUsername(), title, date, time, duration, numberOfParticipants);
        request.getSession().setAttribute("tempMeeting", tempMeeting);
        request.getSession().setAttribute("counter", 0);
        request.getSession().setAttribute("userMap", new HashMap<String, Pair<User, Boolean>>());

        response.sendRedirect(getServletContext().getContextPath() + "/Registry");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    private void sendError(String error, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().setAttribute("errorMessage", error);
        response.sendRedirect(getServletContext().getContextPath() + "/Home");
    }
}
