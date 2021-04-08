package SchedulingApp.DAO;

import SchedulingApp.Model.City;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static SchedulingApp.View_Controller.LoginController.loggedUser;

public class DBCity {
    public static void addCityToDB(City city) throws SQLException { //Returns cityId when set
        String addCitySQL = String.join(" ",
                "INSERT INTO city (cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy)",
                "VALUES (?, ?, ?, NOW(), ?, NOW(), ?)");
        int cityId = getMaxCityId();
        city.setCityId(cityId);
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(addCitySQL);
            statement.setInt(1, cityId); //Set by us in this function
            statement.setString(2, city.getCity());        //Get user which added the customer
            statement.setInt(3, city.getCountryId()); //Set by us in this function
            statement.setString(4, loggedUser.getUserName());        //Get user which added the customer
            statement.setString(5, loggedUser.getUserName());        //Get user which added the customer
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        //return city;
    }

    private static int getMaxCityId() {
        int maxCityId = 0;
        String maxCityIdSQL = "SELECT MAX(cityId) FROM city";
        try {
            Statement statement = DBConnector.connection.createStatement();
            ResultSet resultSet = statement.executeQuery(maxCityIdSQL);

            if (resultSet.next()) {
                maxCityId = resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
        }
        return maxCityId + 1;
    }

    public static void modifyCityInDB(City city) {
        String addCitySQL = "UPDATE city SET city = ?, countryId = ?, lastUpdate = NOW(), lastUpdateBy = ? WHERE cityId = " + city.getCityId();
        try {
            PreparedStatement statement = DBConnector.connection.prepareStatement(addCitySQL);
            statement.setString(1, city.getCity());        //Get user which added the customer
            statement.setInt(2, city.getCountryId()); //Set by us in this function
            statement.setString(3, loggedUser.getUserName());        //Get user which added the customer
            statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static City getCityById(int cityId) throws SQLException {
        City city = new City();
        city.setCityId(cityId);

        String getCitySQL = "SELECT * FROM city WHERE cityId = " + cityId;
        Statement statement = DBConnector.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(getCitySQL);
        while (resultSet.next()){
            city.setCity(resultSet.getString(2));
            city.setCountryId(resultSet.getInt(3));
        }
        return city;
    }
}
