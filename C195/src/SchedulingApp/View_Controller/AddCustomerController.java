package SchedulingApp.View_Controller;

import SchedulingApp.DAO.*;
import SchedulingApp.Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField address1Field;
    @FXML private TextField address2Field;
    @FXML private TextField cityField;
    @FXML private ComboBox<Country> countryBox;
    @FXML private TextField postalField;
    @FXML private TextField phoneField;

    //Converts objects in countryBox to strings
    StringConverter<Country> countryToString = new StringConverter<Country>() {
        @Override
        public String toString(Country country) { return country.getCountry(); }

        @Override
        public Country fromString(String string) {
            return countryBox.getValue();
        }
    };

    Customer customer = new Customer();
    Address address = new Address();
    City city = new City();
    Country country = new Country();

    public void initialize(URL url, ResourceBundle rb) {
        try {
            countryBox.setItems(DBCountry.getAllCountries());
            countryBox.setValue(countryBox.getItems().get(0));
            countryBox.setConverter(countryToString);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML private void addButtonClicked(ActionEvent event) throws SQLException, IOException {
        if (setupObjects()){
            DBCity.addCityToDB(city);
            address.setCityId(city.getCityId());
            DBAddress.addAddressToDB(address);
            customer.setAddressId(address.getAddressId());
            DBCustomer.addCustomerToDB(customer);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
            Parent parent = loader.load();
            Scene addProductPageScene = new Scene(parent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addProductPageScene);
        }
    }

    private boolean setupObjects(){
        String name = nameField.getText().strip();
        String address1 = address1Field.getText().strip();
        String address2 = address2Field.getText().strip();
        String postal = postalField.getText().strip();
        String phone = phoneField.getText().strip();
        String cityString = cityField.getText().strip();
        country = countryBox.getSelectionModel().getSelectedItem();

        //Country will never be null because default selection is set to the first item...
        if (name.isEmpty() || address1.isEmpty() || address2.isEmpty() || postal.isEmpty() || phone.isEmpty() || cityString.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Please fill-out all fields for the customer. Put 'NA' if a field is not applicable.");
            alert.showAndWait();
            return false;
        }

        customer.setCustomerName(name);
        address.setAddress(address1);
        address.setAddress2(address2);
        address.setPostalCode(postal);
        address.setPhone(phone);
        city.setCity(cityString);
        city.setCountryId(country.getCountryId());
        return true;
    }

    @FXML private void cancelButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
        Parent parent = loader.load();
        Scene addProductPageScene = new Scene(parent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addProductPageScene);
    }


}
