package it.polimi.tiw.tiwproject.dao;

import it.polimi.tiw.tiwproject.beans.User;
import it.polimi.tiw.tiwproject.utilities.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User checkCredentials(String username, String password) throws SQLException {
        String query = "SELECT  idUser, username FROM db_tiw_project.user WHERE username = ? AND password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet result = preparedStatement.executeQuery()){
                if(!result.isBeforeFirst()) return null;
                else {
                    result.next();
                    User user = new User();
                    user.setId(result.getInt("idUser"));
                    user.setUsername(result.getString("username"));
                    return user;
                }
            }
        }
    }

    public void createUser(String email, String username, String password) throws SQLException {
        String query = "INSERT into db_tiw_project.user (email, username, password) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        }
    }

    public HashMap<String, Pair<User, Boolean>> addNewUsers(User creator, HashMap<String, Pair<User, Boolean>> userMap) throws SQLException {
        String query = "SELECT idUser, username FROM db_tiw_project.user WHERE username <> ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, creator.getUsername());

            try (ResultSet result = preparedStatement.executeQuery()){
                while(result.next()){
                    if(!userMap.containsKey(result.getString("username"))) {
                        User user = new User();
                        user.setId(result.getInt("idUser"));
                        user.setUsername(result.getString("username"));

                        userMap.put(result.getString("username"), new Pair<>(user, Boolean.FALSE));
                    }
                }
            }
        }

        return userMap;
    }
}
