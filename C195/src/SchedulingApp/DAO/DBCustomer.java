package SchedulingApp.DAO;

import SchedulingApp.Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

import static SchedulingApp.View_Controller.LoginController.loggedUser;

//Contains public observable list of activeCustomers in the database
public class DBCustomer {

    public static ObservableList<Customer> getAllActiveCustomers() throws SQLException {
        ObservableList<Customer> activeCustomers = FXCollections.observableArrayList();

        Statement queryStatement = DBConnector.connection.createStatement();
        ResultSet resultSet = queryStatement.executeQuery("SELECT * FROM customer WHERE active = 1");
        while (resultSet.next()){
            Customer customer = new Customer();
            customer.setCustomerId(resultSet.getInt(1));
            customer.setCustomerName(resultSet.getString(2));
            customer.setAddressId(resultSet.getInt(3));
            activeCustomers.add(customer);
        }
        return activeCustomers;
    }

    public static Customer getCustomerByID(int id) throws SQLException {
        Customer customer = new Customer();
        Statement queryStatement = DBConnector.connection.createStatement();
        ResultSet resultSet = queryStatement.executeQuery("SELECT * FROM customer WHERE active = 1 AND customerId = " + id);
        while (resultSet.next()){
            customer.setCustomerId(resultSet.getInt(1));
            customer.setCustomerName(resultSet.getString(2));
            customer.setAddressId(resultSet.getInt(3));
        }
        return customer;
    }

    public static void addCustomerToDB(Customer customer) throws SQLException {

        String addCustomerSQL = String.join(" ",
                "INSERT INTO customer (customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)",
                "VALUES (?, ?, ?, 1, NOW(), ?, NOW(), ?)");
        int customerId = getMaxCustomerId();
        customer.setCustomerId(customerId);
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(addCustomerSQL);
            statement.setInt(1, customerId); //Set by us in this function
            statement.setString(2, customer.getCustomerName());      //Preset already
            statement.setInt(3, customer.getAddressId());            //Preset already
            statement.setString(4, loggedUser.getUserName());        //Get user which added the customer
            statement.setString(5, loggedUser.getUserName());        //Get user which added the customer
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void modifyCustomerInDB(Customer customer){
        String addCitySQL = "UPDATE customer SET customerName = ?, addressId = ?, lastUpdate = NOW(), lastUpdateBy = ? WHERE customerId = " + customer.getCustomerId();
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(addCitySQL);
            statement.setString(1, customer.getCustomerName());        //Get user which added the customer
            statement.setInt(2, customer.getAddressId()); //Set by us in this function
            statement.setString(3, loggedUser.getUserName());        //Get user which added the customer
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int deleteCustomerInDB(Customer customer) throws SQLException {
        Statement statement = DBConnector.connection.createStatement();
        String deleteCustomerSQL = "UPDATE customer SET active = 0 WHERE customerId = " + customer.getCustomerId();
        return(statement.executeUpdate(deleteCustomerSQL));
    }

    private static int getMaxCustomerId() {
        int maxCustomerId = 0;
        String maxCustomerIdSQL = "SELECT MAX(customerId) FROM customer";

        try {
            Statement statement = DBConnector.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(maxCustomerIdSQL);

            if (resultSet.next()) {
                maxCustomerId = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
        }
        return maxCustomerId + 1;
    }

}
