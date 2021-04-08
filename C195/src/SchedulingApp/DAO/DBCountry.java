package SchedulingApp.DAO;

import SchedulingApp.Model.Country;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBCountry {

    public static ObservableList<Country> getAllCountries() throws SQLException {
        ObservableList<Country> allCountries = FXCollections.observableArrayList();
        Statement statement = DBConnector.connection.createStatement();
        try{
            String sqlStatement = "SELECT * FROM country";
            ResultSet resultSet = statement.executeQuery(sqlStatement);
            while (resultSet.next()){
                Country country = new Country();
                country.setCountryId(Integer.parseInt(resultSet.getString(1)));
                country.setCountry(resultSet.getString(2));
                allCountries.add(country);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return allCountries;
    }

    public static Country getCountryById(int countryId) throws SQLException {
        Country country = new Country();
        Statement statement = DBConnector.connection.createStatement();
        String getCountrySQL = "SELECT * FROM country WHERE countryId = " + countryId;
        ResultSet resultSet = statement.executeQuery(getCountrySQL);
        while (resultSet.next()){
            country.setCountryId(countryId);
            country.setCountry(resultSet.getString(2));
        }
        return country;
    }
}
