package SchedulingApp.View_Controller;

import SchedulingApp.DAO.DBAppointment;
import SchedulingApp.DAO.DBCustomer;
import SchedulingApp.Model.Customer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DeleteCustomerController implements Initializable {
    @FXML private ListView<Customer> customerListView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            customerListView.setItems(DBCustomer.getAllActiveCustomers());
            convertCustomersToString();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void convertCustomersToString() {
        customerListView.setCellFactory(param -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null || customer.getCustomerName() == null) {
                    setText("");
                } else {
                    setText(customer.getCustomerName());

                }

            }
        });
    }

    @FXML private void deleteButtonClicked(ActionEvent event) throws SQLException, IOException {
        Customer customer = customerListView.getSelectionModel().getSelectedItem();
        if (customer == null){ //A customer wasn't selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid selection");
            alert.setHeaderText("Please select a customer to delete.");
            alert.showAndWait();
        }
        else if (DBAppointment.customerHasAppointment(customer)){ //If customer has an appointment
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Trying to delete customer with appointment");
            alert.setHeaderText("The customer you are trying to delete has an appointment either in past or future. \nTo delete this customer you must first delete the tied appointment!");
            alert.showAndWait();
        }
        else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Customer");
            alert.setHeaderText("Are you sure you want to delete the selected customer?");
            alert.showAndWait();
            if (alert.getResult() == ButtonType.OK){
                DBCustomer.deleteCustomerInDB(customer); //If we deleted at least one customer return to main menu
                cancelButtonClicked(event); //Run cancelButtonClicked function which will take us back to mainmenu
            }
        }
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
