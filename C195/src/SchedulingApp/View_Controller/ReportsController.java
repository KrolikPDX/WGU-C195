package SchedulingApp.View_Controller;

import SchedulingApp.DAO.DBAppointment;
import SchedulingApp.DAO.DBCustomer;
import SchedulingApp.DAO.DBUser;
import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.Customer;
import SchedulingApp.Model.User;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportsController implements Initializable {

    @FXML private RadioButton radioNumberTypes;
    @FXML private RadioButton radioSchedule;
    @FXML private RadioButton radioAllUsers;
    @FXML private TextArea textArea;

    private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm");

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        radioNumberTypes.setSelected(true);
    }

    @FXML private void reportButtonClicked() throws SQLException {
        textArea.clear();
        if (radioNumberTypes.isSelected()){
            ObservableList<Appointment> appointments = DBAppointment.getMonthAppointment();
            Map<String, Integer> map = new HashMap<>();
            for (Appointment currentAppointment : appointments) {
                if (map.containsKey(currentAppointment.getType()))
                    map.put(currentAppointment.getType(), map.get(currentAppointment.getType())+1);
                else
                    map.put(currentAppointment.getType(), 1);
            }
            for (String type : map.keySet())
                textArea.appendText("There are " + map.get(type) + " appointments of type: " + type + " this month.\n");

        } //If user selected appointment types radio button
        else if (radioSchedule.isSelected()){
            ObservableList<User> allUsers = DBUser.getAllUsers();
            ZonedDateTime startZDT;
            for (User currentUser : allUsers){ //Go through all the users
                ObservableList<Appointment> userSchedule = DBAppointment.getAppointmentsWithUserID(currentUser.getUserId());
                for (Appointment currentAppointment : userSchedule) { //Go through all the appointments for the current user
                    startZDT = currentAppointment.getStart();
                    Customer activeCustomerById = DBCustomer.getCustomerByID(currentAppointment.getCustomerId());
                    textArea.appendText(currentUser.getUserName() + " has an appointment with " + activeCustomerById.getCustomerName()
                            + " on/at: " + startZDT.format(format) + "\n");
                }
            }
        }
        else if (radioAllUsers.isSelected()){
            ObservableList<User> allUsers = DBUser.getAllUsers();
            textArea.setText("All Users: \n");
            for (User currentUser : allUsers){
                textArea.appendText("User: " + currentUser.getUserId() + " has username: '" + currentUser.getUserName() + "'\n");
            }
        }
    }

    @FXML private void homeButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
        Parent parent = loader.load();
        Scene addProductPageScene = new Scene(parent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addProductPageScene);
    }

}
