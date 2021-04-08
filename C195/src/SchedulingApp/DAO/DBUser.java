package SchedulingApp.DAO;

import SchedulingApp.Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUser {
    public static ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        String getAllUserSQL = "SELECT * FROM user";
        Statement statement = DBConnector.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(getAllUserSQL);
        while (resultSet.next()){
            User user = new User();
            user.setUserId(resultSet.getInt(1));
            user.setUserName(resultSet.getString(2));
            allUsers.add(user);
        }
        return allUsers;
    }
}
