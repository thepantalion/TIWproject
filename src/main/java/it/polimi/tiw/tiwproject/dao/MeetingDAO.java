package it.polimi.tiw.tiwproject.dao;

import it.polimi.tiw.tiwproject.beans.Meeting;
import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.utilities.Pair;

import java.sql.*;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;

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

    public void createMeeting(User creator, Meeting meeting, HashMap<String, Pair<User, Boolean>> userMap) throws SQLException {
        ArrayList<User> userList = new ArrayList<>();
        userList.add(creator);
        int meetingID;

        for(String username : userMap.keySet()) {
            if(userMap.get(username).get_2() == Boolean.TRUE) userList.add(userMap.get(username).get_1());
        }

        String meetingQuery = "INSERT into db_tiw_project.meeting (title, date, time, duration, maxNumOfParticipants, idCreator) VALUES (?,?,?,?,?,?)";
        PreparedStatement meetingPreparedStatement = null;

        String inviteeQuery = "INSERT into db_tiw_project.invited (idUser, idMeeting) VALUES(?,?)";
        PreparedStatement inviteePreparedStatement = null;

        try {
            connection.setAutoCommit(false);

            meetingPreparedStatement = connection.prepareStatement(meetingQuery, Statement.RETURN_GENERATED_KEYS);
            meetingPreparedStatement.setString(1, meeting.getTitle());
            meetingPreparedStatement.setObject(2, meeting.getDate().toInstant().atZone(ZoneId.of("Europe/Rome")).toLocalDate());
            meetingPreparedStatement.setTime(3, meeting.getTime());
            meetingPreparedStatement.setInt(4, meeting.getDuration());
            meetingPreparedStatement.setInt(5, meeting.getNumberOfParticipants());
            meetingPreparedStatement.setInt(6, creator.getId());

            int result = meetingPreparedStatement.executeUpdate();
            if(result != 1) throw new SQLException();

            ResultSet resultSet = meetingPreparedStatement.getGeneratedKeys();
            if(resultSet.next()) {
                meetingID = resultSet.getInt(1);

                inviteePreparedStatement = connection.prepareStatement(inviteeQuery);
                inviteePreparedStatement.setInt(2, meetingID);
                for (User user : userList) {
                    inviteePreparedStatement.setInt(1, user.getId());

                    result = inviteePreparedStatement.executeUpdate();
                    if(result != 1) throw new SQLException();
                }
            } else throw new SQLException();
        } catch (Exception exception) {
            connection.rollback();
            throw new SQLException(exception);
        } finally {
            connection.setAutoCommit(true);
            if(meetingPreparedStatement != null) meetingPreparedStatement.close();
            if(inviteePreparedStatement != null) inviteePreparedStatement.close();
        }
    }

}
