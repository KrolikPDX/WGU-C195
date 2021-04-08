package SchedulingApp.View_Controller;

import SchedulingApp.DAO.*;
import SchedulingApp.Model.*;
import javafx.collections.ObservableList;
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
import javafx.util.converter.LocalTimeStringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import static SchedulingApp.View_Controller.LoginController.loggedUser;
import static SchedulingApp.View_Controller.LoginController.zoneID;

public class ModifyAppointmentController implements Initializable {
//region FXML Objects + Init objects
    @FXML private ComboBox<Customer> customerBox;
    @FXML private TextField titleField;
    @FXML private TextField descriptionField;
    @FXML private TextField locationField;
    @FXML private TextField contactField;
    @FXML private TextField typeField;
    @FXML private TextField urlField;
    @FXML private DatePicker startDate;
    @FXML private Spinner<LocalTime> startTime;
    @FXML private DatePicker endDate;
    @FXML private Spinner<LocalTime> endTime;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
    private final ZonedDateTime businessStartHour = ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT.plusHours(8), zoneID);
    private final ZonedDateTime businessEndHour = ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT.plusHours(17), zoneID);


    StringConverter<Customer> customerToString = new StringConverter<Customer>() {
        @Override
        public String toString(Customer cust) {
            return cust.getCustomerName();
        }

        @Override
        public Customer fromString(String string) {
            return customerBox.getValue();
        }
    };
    SpinnerValueFactory startTimeSVF = new SpinnerValueFactory<LocalTime>() {
        { setConverter(new LocalTimeStringConverter(timeFormat,null)); }
        @Override public void decrement(int steps) {
            LocalTime time = getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16 - steps));
        }
        @Override public void increment(int steps) {
            LocalTime time = getValue();
            setValue(time.plusHours(steps));
            setValue(time.plusMinutes(steps + 14));
        }
    };
    SpinnerValueFactory endTimeSVF = new SpinnerValueFactory<LocalTime>() {
        { setConverter(new LocalTimeStringConverter(timeFormat,null)); }
        @Override
        public void decrement(int steps) {
            LocalTime time = getValue();
            setValue(time.minusHours(steps));
            setValue(time.minusMinutes(16 - steps));
        }
        @Override
        public void increment(int steps) {
            LocalTime time = getValue();
            setValue(time.plusHours(steps));
            setValue(time.plusMinutes(steps + 14));
        }
    };

    Appointment selectedAppointment = new Appointment();
    Customer selectedCustomer = new Customer();
//endregion FXML Objects + Init objects

    public void initialize(URL url, ResourceBundle rb) {
        try {
            customerBox.setItems(DBCustomer.getAllActiveCustomers());
            customerBox.setConverter(customerToString);


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @FXML public void initAppointment(Appointment appointment) throws IOException {
        this.selectedAppointment = appointment;
        customerBox.setValue(appointment.getCustomer());
        titleField.setText(appointment.getTitle());
        descriptionField.setText(appointment.getDescription());
        locationField.setText(appointment.getLocation());
        contactField.setText(appointment.getContact());
        typeField.setText(appointment.getType());
        urlField.setText(appointment.getUrl());

        startDate.setValue(appointment.getStart().toLocalDate());
        endDate.setValue(appointment.getEnd().toLocalDate());

        startTime.setValueFactory(startTimeSVF);
        endTime.setValueFactory(endTimeSVF);
        startTimeSVF.setValue(appointment.getStart().toLocalTime());
        endTimeSVF.setValue(appointment.getEnd().toLocalTime());
    }

    @FXML private void modifyButtonClicked(ActionEvent event) throws SQLException, IOException {

        if (setupObject()){
            DBAppointment.modifyAppointmentInDB(selectedAppointment);

            //Go back to home page
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
            Parent parent = loader.load();
            Scene addProductPageScene = new Scene(parent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addProductPageScene);
        }
    }

    private boolean setupObject() throws SQLException {
        selectedCustomer = customerBox.getSelectionModel().getSelectedItem();
        String title = titleField.getText().strip();
        String description = descriptionField.getText().strip();
        String location = locationField.getText().strip();
        String contact = contactField.getText().strip();
        String type = typeField.getText().strip();
        String url = urlField.getText().strip();
        ZonedDateTime startZDT = ZonedDateTime.of(LocalDate.parse(startDate.getValue().toString(), dateFormat), LocalTime.parse(startTime.getValue().toString(), timeFormat), zoneID);
        ZonedDateTime endZDT = ZonedDateTime.of(LocalDate.parse(endDate.getValue().toString(), dateFormat), LocalTime.parse(endTime.getValue().toString(), timeFormat), zoneID);

        //Check exceptions
        if (selectedCustomer ==  null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid selection");
            alert.setHeaderText("Please select a customer to add to the appointment.");
            alert.showAndWait();
            return false;
        }
        else if (title.isEmpty() || description.isEmpty() || location.isEmpty() || contact.isEmpty() || type.isEmpty() || url.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid input");
            alert.setHeaderText("Please fill-out all fields for the appointment. Put 'NA' if a field is not applicable.");
            alert.showAndWait();
            return false;
        }

        //Setup appointment if we made it this far
        selectedAppointment.setCustomerId(selectedCustomer.getCustomerId());
        selectedAppointment.setCustomer(selectedCustomer);
        selectedAppointment.setUserId(loggedUser.getUserId());
        selectedAppointment.setTitle(title);
        selectedAppointment.setDescription(description);
        selectedAppointment.setLocation(location);
        selectedAppointment.setContact(contact);
        selectedAppointment.setType(type);
        selectedAppointment.setUrl(url);
        selectedAppointment.setStart(startZDT);
        selectedAppointment.setEnd(endZDT);

        //One final check is to see if our appointment overlaps any other appointments...
        return (isValidTime()); //returns true if it doesn't overlap other appointments
    }

    public boolean isValidTime() throws SQLException {
        LocalDate startDate = selectedAppointment.getStart().toLocalDate();
        LocalTime startTime = selectedAppointment.getStart().toLocalTime();
        LocalDate endDate = selectedAppointment.getEnd().toLocalDate();
        LocalTime endTime = selectedAppointment.getEnd().toLocalTime();

        if (startTime.isBefore(LocalTime.MIDNIGHT.plusHours(8))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Scheduling outside of business hours");
            alert.setHeaderText("Trying to schedule start-time before 8:00 local time!");
            alert.showAndWait();
            return false;
        }
        if (endTime.isAfter(LocalTime.MIDNIGHT.plusHours(17))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Scheduling outside of business hours");
            alert.setHeaderText("Trying to schedule end-time after 17:00 local time!");
            alert.showAndWait();
            return false;
        }
        if (endTime.isBefore(startTime)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Times");
            alert.setHeaderText("Trying to schedule with an end time before start time!");
            alert.showAndWait();
            return false;
        }
        if (startDate.isBefore(LocalDate.now())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Date");
            alert.setHeaderText("Trying to schedule start-date before today");
            alert.showAndWait();
            return false;
        }
        if (endDate.isBefore(startDate)){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Date");
            alert.setHeaderText("End-date is before Start-date!");
            alert.showAndWait();
            return false;
        }
        return notOverlappingOthers(selectedAppointment); //If all tests pass above, do a final check to see if any appointments overlap
    }

    //makes sure selected dates do not overlap other appointments other than itself
    boolean notOverlappingOthers(Appointment appointment) throws SQLException {
        ObservableList<Appointment> allAppointments = DBAppointment.getAllAppointments();
        for (Appointment currentAppointment : allAppointments){
            if (appointment.getStart().isBefore(currentAppointment.getEnd()) && appointment.getEnd().isAfter(currentAppointment.getStart()) && currentAppointment.getAppointmentId()!=appointment.getAppointmentId()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Overlapping appointments");
                alert.setHeaderText("You are trying to schedule an appointment which overlaps with " + currentAppointment.getCustomer().getCustomerName() + "'s appointment " +
                        "\nwhich starts at " + currentAppointment.getStart().format(timeFormat) + " " + currentAppointment.getStart().format(dateFormat) +
                        " and ends at " + currentAppointment.getEnd().format(timeFormat) + " " + currentAppointment.getEnd().format(dateFormat));
                alert.showAndWait();
                return false;
            }

        }
        return true;
    }
    //When user chooses a customer update fields with the info
    @FXML private void selectedCustomerClicked() throws SQLException {
        selectedCustomer = customerBox.getSelectionModel().getSelectedItem();
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
