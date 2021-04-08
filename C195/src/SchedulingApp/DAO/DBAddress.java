package SchedulingApp.DAO;

import SchedulingApp.Model.Address;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static SchedulingApp.View_Controller.LoginController.loggedUser;

public class DBAddress {


    public static void addAddressToDB(Address address) throws SQLException {
        String addCustomerSQL = String.join(" ",
                "INSERT INTO address (addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)",
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), ?)");
        int addressId = getMaxAddressId();
        address.setAddressId(addressId);
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(addCustomerSQL);
            statement.setInt(1, addressId);
            statement.setString(2, address.getAddress());
            statement.setString(3, address.getAddress2());
            statement.setInt(4, address.getCityId());
            statement.setString(5, address.getPostalCode());
            statement.setString(6, address.getPhone());
            statement.setString(7, loggedUser.getUserName());
            statement.setString(8, loggedUser.getUserName());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static int getMaxAddressId() {
        int maxAddressId = 0;
        String maxAddressIdSQL = "SELECT MAX(addressId) FROM address";
        try {
            Statement statement = DBConnector.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(maxAddressIdSQL);

            if (resultSet.next()) {
                maxAddressId = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
        }
        return maxAddressId + 1;
    }

    public static Address getAddressById(int id) throws SQLException {
        Address address = new Address();
        Statement statement = DBConnector.connection.createStatement();
        String getAddressSQL = "SELECT * FROM address WHERE addressId = " + id;
        ResultSet resultSet = statement.executeQuery(getAddressSQL);
        while (resultSet.next()){
            address.setAddressId(id);
            address.setAddress(resultSet.getString(2));
            address.setAddress2(resultSet.getString(3));
            address.setCityId(Integer.parseInt(resultSet.getString(4)));
            address.setPostalCode(resultSet.getString(5));
            address.setPhone(resultSet.getString(6));
        }
        return address;
    }

    public static void modifyAddressInDB(Address address) {
        String addCitySQL = "UPDATE address SET address = ?, address2 = ?, cityId = ?, postalCode = ?, phone = ?, lastUpdate = NOW(), lastUpdateBy = ? WHERE cityId = " + address.getAddressId();
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(addCitySQL);
            statement.setString(1, address.getAddress());        //Get user which added the customer
            statement.setString(2, address.getAddress2()); //Set by us in this function
            statement.setInt(3, address.getCityId());        //Get user which added the customer
            statement.setString(4, address.getPostalCode());
            statement.setString(5, address.getPhone());
            statement.setString(6, loggedUser.getUserName());
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
