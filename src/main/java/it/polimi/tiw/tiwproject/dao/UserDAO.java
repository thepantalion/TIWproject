package it.polimi.tiw.tiwproject.dao;

import it.polimi.tiw.tiwproject.beans.User;

import java.sql.*;

public class UserDAO {
    private final Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public User checkCredentials(String username, String password) throws SQLException {
        String query = "SELECT  idUser, username, email FROM db_tiw_project.user WHERE username = ? AND password = ?";
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
                    user.setEmail(result.getString("email"));
                    return user;
                }
            }
        }
    }

    public void createUser(String email, String username, String password) throws SQLException {

        String query = "INSERT into db_tiw_project.user (email, username, password) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, password);
            preparedStatement.executeUpdate();
        }
    }
}
