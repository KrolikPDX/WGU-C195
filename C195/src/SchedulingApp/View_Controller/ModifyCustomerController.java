package SchedulingApp.View_Controller;

import SchedulingApp.DAO.DBAddress;
import SchedulingApp.DAO.DBCity;
import SchedulingApp.DAO.DBCountry;
import SchedulingApp.DAO.DBCustomer;
import SchedulingApp.Model.Address;
import SchedulingApp.Model.City;
import SchedulingApp.Model.Country;
import SchedulingApp.Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifyCustomerController implements Initializable {
    @FXML private TextField nameField;
    @FXML private TextField address1Field;
    @FXML private TextField address2Field;
    @FXML private TextField cityField;
    @FXML private ComboBox<Customer> customerBox;
    @FXML private ComboBox<Country> countryBox;
    @FXML private TextField postalField;
    @FXML private TextField phoneField;
    @FXML private ChoiceBox<Customer> choiceBox;

    //Converts objects in CustomerBox to strings
    StringConverter<Customer> customerToString = new StringConverter<Customer>() {
        @Override
        public String toString(Customer customer) {
            return customer.getCustomerName();
        }

        @Override
        public Customer fromString(String string) {
            return customerBox.getValue();
        }
    };
    //Converts objects in CountryBox to strings
    StringConverter<Country> countryToString = new StringConverter<Country>() {
        @Override
        public String toString(Country country) { return country.getCountry(); }

        @Override
        public Country fromString(String string) {
            return countryBox.getValue();
        }
    };

    Customer selectedCustomer = new Customer();
    Address address = new Address();
    City city = new City();
    Country country = new Country();


    public void initialize(URL url, ResourceBundle rb) {
        try {
            customerBox.setItems(DBCustomer.getAllActiveCustomers());
            customerBox.setConverter(customerToString);
            countryBox.setItems(DBCountry.getAllCountries());
            countryBox.setConverter(countryToString);
            countryBox.setDisable(true);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML private void modifyButtonClicked(ActionEvent event) throws SQLException, IOException {

        if (setupObjects()){
            DBCity.modifyCityInDB(city);
            DBAddress.modifyAddressInDB(address);
            DBCustomer.modifyCustomerInDB(selectedCustomer);

            //Go back to home page
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
            Parent parent = loader.load();
            Scene addProductPageScene = new Scene(parent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addProductPageScene);
        }

    }

    private boolean setupObjects(){
        selectedCustomer = customerBox.getSelectionModel().getSelectedItem();
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
            alert.setTitle("INVALID INPUT");
            alert.setHeaderText("Please fill-out all fields for the customer. Put 'NA' if a field is not applicable.");
            alert.showAndWait();
            return false;
        }
        else if (selectedCustomer == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Selection");
            alert.setHeaderText("Please select a customer to modify.");
            alert.showAndWait();
            return false;
        }

        selectedCustomer.setCustomerName(name);
        address.setAddress(address1);
        address.setAddress2(address2);
        address.setPostalCode(postal);
        address.setPhone(phone);
        city.setCity(cityString);
        city.setCountryId(country.getCountryId());
        return true;
    }

    @FXML private void selectedCustomerClicked() throws SQLException { //When user chooses a customer update fields with the info
        selectedCustomer = customerBox.getSelectionModel().getSelectedItem();
        address = DBAddress.getAddressById(selectedCustomer.getAddressId());
        city = DBCity.getCityById(address.getCityId());
        country = DBCountry.getCountryById(city.getCountryId());
        nameField.setText(selectedCustomer.getCustomerName());
        address1Field.setText(address.getAddress());
        address2Field.setText(address.getAddress2());
        cityField.setText(city.getCity());

        countryBox.setValue(country);
        countryBox.setDisable(false);

        postalField.setText(address.getPostalCode());
        phoneField.setText(address.getPhone());

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
