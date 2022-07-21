package it.polimi.tiw.tiwproject.controllers;

import it.polimi.tiw.tiwproject.beans.Meeting;
import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.dao.MeetingDAO;
import it.polimi.tiw.tiwproject.utilities.ConnectionHandler;
import it.polimi.tiw.tiwproject.utilities.Pair;
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@WebServlet("/CreateMeeting")
public class CreateMeeting extends HttpServlet {
    private Connection connection;
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        boolean noSelection = false;

        if(request.getParameterValues("selectedUsers") == null) noSelection = true;

        User user = (User) session.getAttribute("user");
        Meeting tempMeeting = (Meeting) session.getAttribute("tempMeeting");
        HashMap<String, Pair<User, Boolean>> userMap = (HashMap<String, Pair<User, Boolean>>) session.getAttribute("userMap");

        ArrayList<String> selectedUsers;
        if(!noSelection) {
            selectedUsers = new ArrayList<>(Arrays.asList(request.getParameterValues("selectedUsers")));

            for (String username : userMap.keySet()) {
                userMap.get(username).set_2(Boolean.FALSE);
            }

            for (String username : selectedUsers) {
                if (userMap.containsKey(username)) {
                    userMap.get(username).set_2(Boolean.TRUE);
                }
            }

            session.setAttribute("userMap", userMap);
        } else selectedUsers = new ArrayList<>();

        if(selectedUsers.size() > tempMeeting.getNumberOfParticipants() - 1 || selectedUsers.size() <= 0){
            int counter = (int) session.getAttribute("counter");
            counter++;
            session.setAttribute("counter", counter);

            if(counter <= 2){
                if(noSelection) {
                    session.setAttribute("errorMessage", "You didn't select any user. Please do so. (available attempts: " + (3 - counter) + ")");
                    response.sendRedirect(getServletContext().getContextPath() + "/Registry");
                } else {
                    session.setAttribute("errorMessage", "You selected too many users. Please unselect at least " + (selectedUsers.size() - tempMeeting.getNumberOfParticipants() + 1) + " users. (available attempts: " + (3 - counter) + ")");
                    response.sendRedirect(getServletContext().getContextPath() + "/Registry");
                }
            } else {
                response.sendRedirect(getServletContext().getContextPath() + "/Undo");
            }

            return;
        }

        try{
            MeetingDAO meetingDAO = new MeetingDAO(connection);
            meetingDAO.createMeeting(user, tempMeeting, userMap);
        } catch (Exception exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was a problem with the database. :(");
            return;
        }

        response.sendRedirect(getServletContext().getContextPath() + "/Home");
    }
}
