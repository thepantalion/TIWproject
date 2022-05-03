package it.polimi.tiw.tiwproject.dao;

import it.polimi.tiw.tiwproject.beans.Meeting;
import it.polimi.tiw.tiwproject.beans.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MeetingDAO {
    private final Connection connection;

    public MeetingDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Meeting> meetingsCreated(User creator) throws SQLException {
        String query = "SELECT meeting.title, meeting.date, meeting.time, meeting.duration " +
                       "FROM db_tiw_project.user JOIN db_tiw_project.meeting " +
                       "ON user.idUser = meeting.idCreator " +
                       "WHERE (curdate() < meeting.date OR (curdate() = meeting.date AND curtime() < meeting.time)) AND user.idUser = ?";
        ArrayList<Meeting> meetingList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, creator.getId());

            try (ResultSet result = preparedStatement.executeQuery()){
                while(result.next()){
                    Meeting meeting = new Meeting();
                    meeting.setTitle(result.getString("title"));
                    meeting.setDate(result.getDate("date"));
                    meeting.setTime(result.getTime("time"));
                    meeting.setDuration(result.getInt("duration"));
                    meetingList.add(meeting);
                }
            }
        }

        return meetingList;
    }

    public ArrayList<Meeting> meetingsInvited(User invitee) throws SQLException {
        String query = "SELECT userCreator.username, meeting.title, meeting.date, meeting.time, meeting.duration " +
                       "FROM db_tiw_project.user AS userInvited JOIN db_tiw_project.invited " +
                       "ON userInvited.idUser = invited.idUser JOIN db_tiw_project.meeting " +
                       "ON invited.idMeeting = meeting.idmeeting JOIN db_tiw_project.user AS userCreator " +
                       "ON userCreator.idUser = meeting.idCreator WHERE userInvited.idUser = ? AND userInvited.idUser <> meeting.idCreator " +
                       "AND (curdate() < meeting.date OR (curdate() = meeting.date AND curtime() < meeting.time))";
        ArrayList<Meeting> meetingList = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, invitee.getId());

            try (ResultSet result = preparedStatement.executeQuery()){
                while(result.next()){
                    Meeting meeting = new Meeting();
                    meeting.setTitle(result.getString("title"));
                    meeting.setDate(result.getDate("date"));
                    meeting.setTime(result.getTime("time"));
                    meeting.setDuration(result.getInt("duration"));
                    meeting.setCreator(result.getString("username"));
                    meetingList.add(meeting);
                }
            }
        }

        return meetingList;
    }

}
