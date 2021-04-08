package SchedulingApp.DAO;

import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import static SchedulingApp.View_Controller.LoginController.zoneID;
import static SchedulingApp.View_Controller.LoginController.loggedUser;

public class DBAppointment {
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Statement queryStatement = DBConnector.connection.createStatement();
        String getAllAppointmentSQL = "SELECT * FROM appointment";
        ResultSet resultSet = queryStatement.executeQuery(getAllAppointmentSQL);
        while (resultSet.next()){
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(resultSet.getInt("appointmentId"));

            appointment.setCustomer(DBCustomer.getCustomerByID(resultSet.getInt("customerId")));
            appointment.setCustomerId(resultSet.getInt("customerId"));

            appointment.setUserId(resultSet.getInt("userId"));
            appointment.setTitle(resultSet.getString("title"));
            appointment.setDescription(resultSet.getString("description"));
            appointment.setLocation(resultSet.getString("location"));
            appointment.setContact(resultSet.getString("contact"));
            appointment.setType(resultSet.getString("type"));
            appointment.setUrl(resultSet.getString("url"));

            LocalDateTime startUTC = resultSet.getTimestamp("start").toLocalDateTime();
            LocalDateTime endUTC = resultSet.getTimestamp("end").toLocalDateTime();
            ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneID);
            ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneID);

            appointment.setStart(startLocal);
            appointment.setEnd(endLocal);
            allAppointments.add(appointment);
        }
        return allAppointments;
    }

    public static ObservableList<Appointment> getAppointmentsWithUserID(int userId) throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Statement queryStatement = DBConnector.connection.createStatement();
        ResultSet resultSet = queryStatement.executeQuery("SELECT * FROM appointment WHERE userId = " + userId);
        while (resultSet.next()){
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(resultSet.getInt(1));

            appointment.setCustomer(DBCustomer.getCustomerByID(resultSet.getByte(2)));
            appointment.setCustomerId(resultSet.getInt(2));

            appointment.setUserId(resultSet.getInt(3));
            appointment.setTitle(resultSet.getString(4));
            appointment.setDescription(resultSet.getString(5));
            appointment.setLocation(resultSet.getString(6));
            appointment.setContact(resultSet.getString(7));
            appointment.setType(resultSet.getString(8));
            appointment.setUrl(resultSet.getString(9));

            LocalDateTime startUTC = resultSet.getTimestamp("start").toLocalDateTime();
            LocalDateTime endUTC = resultSet.getTimestamp("end").toLocalDateTime();
            ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneID);
            ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneID);

            appointment.setStart(startLocal);
            appointment.setEnd(endLocal);

            allAppointments.add(appointment);
        }
        return allAppointments;
    }

    public static ObservableList<Appointment> getWeekAppointment() throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Statement queryStatement = DBConnector.connection.createStatement();
        String getByWeekSQL = "SELECT customer.*, appointment.* FROM customer "
                + "RIGHT JOIN appointment ON customer.customerId = appointment.customerId "
                + "WHERE start BETWEEN NOW() AND (SELECT ADDDATE(NOW(), INTERVAL 7 DAY))";
        ResultSet resultSet = queryStatement.executeQuery(getByWeekSQL);
        while (resultSet.next()){
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(resultSet.getInt("appointmentId"));

            appointment.setCustomer(DBCustomer.getCustomerByID(resultSet.getInt("customerId")));
            appointment.setCustomerId(resultSet.getInt("customerId"));

            appointment.setUserId(resultSet.getInt("userId"));
            appointment.setTitle(resultSet.getString("title"));
            appointment.setDescription(resultSet.getString("description"));
            appointment.setLocation(resultSet.getString("location"));
            appointment.setContact(resultSet.getString("contact"));
            appointment.setType(resultSet.getString("type"));
            appointment.setUrl(resultSet.getString("url"));

            LocalDateTime startUTC = resultSet.getTimestamp("start").toLocalDateTime();
            LocalDateTime endUTC = resultSet.getTimestamp("end").toLocalDateTime();
            ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneID);
            ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneID);

            appointment.setStart(startLocal);
            appointment.setEnd(endLocal);
            allAppointments.add(appointment);
        }
        return allAppointments;
    }

    public static ObservableList<Appointment> getMonthAppointment() throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        Statement queryStatement = DBConnector.connection.createStatement();
        String getByMonthSQL = "SELECT customer.*, appointment.* FROM customer "
                + "RIGHT JOIN appointment ON customer.customerId = appointment.customerId "
                + "WHERE start BETWEEN NOW() AND (SELECT LAST_DAY(NOW()))";
        ResultSet resultSet = queryStatement.executeQuery(getByMonthSQL);
        while (resultSet.next()){
            Appointment appointment = new Appointment();
            appointment.setAppointmentId(resultSet.getInt("appointmentId"));

            appointment.setCustomer(DBCustomer.getCustomerByID(resultSet.getInt("customerId")));
            appointment.setCustomerId(resultSet.getInt("customerId"));

            appointment.setUserId(resultSet.getInt("userId"));
            appointment.setTitle(resultSet.getString("title"));
            appointment.setDescription(resultSet.getString("description"));
            appointment.setLocation(resultSet.getString("location"));
            appointment.setContact(resultSet.getString("contact"));
            appointment.setType(resultSet.getString("type"));
            appointment.setUrl(resultSet.getString("url"));

            LocalDateTime startUTC = resultSet.getTimestamp("start").toLocalDateTime();
            LocalDateTime endUTC = resultSet.getTimestamp("end").toLocalDateTime();
            ZonedDateTime startLocal = ZonedDateTime.ofInstant(startUTC.toInstant(ZoneOffset.UTC), zoneID);
            ZonedDateTime endLocal = ZonedDateTime.ofInstant(endUTC.toInstant(ZoneOffset.UTC), zoneID);

            appointment.setStart(startLocal);
            appointment.setEnd(endLocal);

            allAppointments.add(appointment);
        }
        return allAppointments;
    }

    public static void addAppointmentToDB(Appointment appointment) throws SQLException {

        String addAppointmentSQL = String.join(" ",
                "INSERT INTO appointment (appointmentId, customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)",
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)");
        int appointmentId = getMaxAppointmentID();
        appointment.setAppointmentId(appointmentId);
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(addAppointmentSQL);
            statement.setInt(1, appointmentId); //Set by us in this function
            statement.setInt(2, appointment.getCustomerId()); //Set by us in this function
            statement.setInt(3, appointment.getUserId());      //Preset already
            statement.setString(4, appointment.getTitle());            //Preset already
            statement.setString(5, appointment.getDescription());            //Preset already
            statement.setString(6, appointment.getLocation());            //Preset already
            statement.setString(7, appointment.getContact());            //Preset already
            statement.setString(8, appointment.getType());            //Preset already
            statement.setString(9, appointment.getUrl());            //Preset already

            ZonedDateTime startZDT = appointment.getStart().withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endZDT = appointment.getEnd().withZoneSameInstant(ZoneId.of("UTC"));
            statement.setTimestamp(10, Timestamp.valueOf(startZDT.toLocalDateTime()));
            statement.setTimestamp(11, Timestamp.valueOf(endZDT.toLocalDateTime()));

            statement.setString(12, loggedUser.getUserName());        //Get user which added the customer
            statement.setString(13, loggedUser.getUserName());        //Get user which added the customer
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static boolean customerHasAppointment(Customer customer) throws SQLException {
        String searchAppointmentSQL = "SELECT * FROM appointment WHERE customerId = " + customer.getCustomerId();
        Statement statement = DBConnector.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(searchAppointmentSQL);
        while (resultSet.next()){
            return true;
        }
        return false;

    }

    public static void modifyAppointmentInDB(Appointment appointment){
        String updateAppointmentSQL = "UPDATE appointment SET customerId = ?, title = ?, description = ?, location = ?, " +
                "contact = ?, type = ?, url = ?, start = ?, end = ?, lastUpdate = NOW(), lastUpdateBy = ? WHERE appointmentId = "
                + appointment.getAppointmentId();
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(updateAppointmentSQL);
            statement.setInt(1, appointment.getCustomer().getCustomerId());
            statement.setString(2, appointment.getTitle());
            statement.setString(3, appointment.getDescription());
            statement.setString(4, appointment.getLocation());
            statement.setString(5, appointment.getContact());
            statement.setString(6, appointment.getType());
            statement.setString(7, appointment.getUrl());

            ZonedDateTime startZDT = appointment.getStart().withZoneSameInstant(ZoneId.of("UTC"));
            ZonedDateTime endZDT = appointment.getEnd().withZoneSameInstant(ZoneId.of("UTC"));
            statement.setTimestamp(8, Timestamp.valueOf(startZDT.toLocalDateTime()));
            statement.setTimestamp(9, Timestamp.valueOf(endZDT.toLocalDateTime()));

            statement.setString(10, loggedUser.getUserName());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int deleteAppointmentInDB(Appointment appointment) throws SQLException {
        String deleteAppointmentSQL = "DELETE FROM appointment WHERE appointmentId = " + appointment.getAppointmentId();
        Statement statement = DBConnector.connection.createStatement();
        return (statement.executeUpdate(deleteAppointmentSQL));
    }

    private static int getMaxAppointmentID() {
        int maxAppointmentID = 0;
        String maxCustomerIdSQL = "SELECT MAX(appointmentId) FROM appointment";

        try {
            Statement statement = DBConnector.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(maxCustomerIdSQL);

            if (resultSet.next()) {
                maxAppointmentID = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
        }
        return maxAppointmentID + 1;
    }



}
