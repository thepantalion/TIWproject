package it.polimi.tiw.tiwproject.controllers;

import org.thymeleaf.TemplateEngine;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.sql.Connection;

@WebServlet("/CreateMeeting")
public class CreateMeeting extends HttpServlet {
    private final Connection connection = null;
    private TemplateEngine templateEngine;

    public CreateMeeting() {
        super();
    }
}
