package SchedulingApp.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnector {
    private static String DB_URL = "jdbc:mysql://wgudb.ucertify.com:3306/U06w91?autoReconnect=true";
    private static String USERNAME = "U06w91";
    private static String PASSWORD = "53688889921";

    public static Connection connection;

    public static void openConnection() throws ClassNotFoundException, SQLException, Exception
    {
        connection = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

        System.out.println("Connection Opened Successfully");
    }
    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception
    {
        connection.close();
        System.out.println("Connection Closed Successfully");
    }

}
