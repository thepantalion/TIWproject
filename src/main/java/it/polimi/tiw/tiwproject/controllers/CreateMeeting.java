package it.polimi.tiw.tiwproject.controllers;

import it.polimi.tiw.tiwproject.utilities.ConnectionHandler;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@WebServlet("/CreateMeeting")
public class CreateMeeting extends HttpServlet {
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public CreateMeeting() {
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

        try {
            title = StringEscapeUtils.escapeJava(request.getParameter("title"));
            duration = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("duration")));
            time = Time.valueOf(StringEscapeUtils.escapeJava(request.getParameter("time")));
            date = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("date"));
            numberOfParticipants = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("numberOfParticipants")));

            if (title == null || duration == 0 || time == null || date == null || numberOfParticipants == 0) throw new Exception();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
            return;
        }

        if (title.isEmpty() || duration <= 0 || (date.equals(Calendar.getInstance().getTime()) && time.before(Calendar.getInstance().getTime()))
                || date.before(Calendar.getInstance().getTime()) || numberOfParticipants < 1){

        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }
}
