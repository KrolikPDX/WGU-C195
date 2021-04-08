package SchedulingApp.View_Controller;

import SchedulingApp.DAO.DBAppointment;
import SchedulingApp.DAO.DBCustomer;
import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.Customer;
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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import static SchedulingApp.View_Controller.LoginController.zoneID;

import static SchedulingApp.View_Controller.LoginController.loggedUser;

public class AddAppointmentController implements Initializable {

//region FXML and Variables
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




    //Converts customerBox objects to string
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
    //SVF used to set Spinner times
    SpinnerValueFactory startTimeSVF = new SpinnerValueFactory<LocalTime>() {
        {
            setConverter(new LocalTimeStringConverter(timeFormat,null));
        }
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
        {
            setConverter(new LocalTimeStringConverter(timeFormat,null));
        }
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
    Appointment appointment = new Appointment();
//endregion FXML and Variables

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            customerBox.setItems(DBCustomer.getAllActiveCustomers());
            customerBox.setConverter(customerToString);

            startDate.setValue(LocalDate.now());
            endDate.setValue(LocalDate.now());

            startTime.setValueFactory(startTimeSVF);
            endTime.setValueFactory(endTimeSVF);

            startTimeSVF.setValue(LocalTime.of(8, 00)); //Set default displayed time for start time
            endTimeSVF.setValue(LocalTime.of(17, 00));  //Set default displayed time for end time

        } catch (SQLException throwables) { throwables.printStackTrace(); }
    }

    @FXML private void addButtonClicked(ActionEvent event) throws SQLException, IOException {
        if (setupAppointment()){
            DBAppointment.addAppointmentToDB(appointment);

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
            Parent parent = loader.load();
            Scene addProductPageScene = new Scene(parent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addProductPageScene);
        }
    }

    //Returns true if everything was setup correctly (no bad user input)
    boolean setupAppointment() throws SQLException {
        Customer customer = customerBox.getSelectionModel().getSelectedItem();
        //Get fields
        String title = titleField.getText().strip();
        String description = descriptionField.getText().strip();
        String location = locationField.getText().strip();
        String contact = contactField.getText().strip();
        String type = typeField.getText().strip();
        String url = urlField.getText().strip();
        ZonedDateTime startZDT = ZonedDateTime.of(LocalDate.parse(startDate.getValue().toString(), dateFormat), LocalTime.parse(startTime.getValue().toString(), timeFormat), zoneID);
        ZonedDateTime endZDT = ZonedDateTime.of(LocalDate.parse(endDate.getValue().toString(), dateFormat), LocalTime.parse(endTime.getValue().toString(), timeFormat), zoneID);

        //Check exceptions
        if (customer ==  null){
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
        appointment.setCustomerId(customer.getCustomerId());
        appointment.setUserId(loggedUser.getUserId());
        appointment.setTitle(title);
        appointment.setDescription(description);
        appointment.setLocation(location);
        appointment.setContact(contact);
        appointment.setType(type);
        appointment.setUrl(url);
        appointment.setStart(startZDT);
        appointment.setEnd(endZDT);
        return (isValidTime()); //Check if appointment has everything setup correctly
    }

    public boolean isValidTime() throws SQLException {
        LocalDate startDate = appointment.getStart().toLocalDate();
        LocalTime startTime = appointment.getStart().toLocalTime();
        LocalDate endDate = appointment.getEnd().toLocalDate();
        LocalTime endTime = appointment.getEnd().toLocalTime();

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
        return notOverlappingOthers(appointment); //If all tests pass above, do a final check to see if any appointments overlap
    }

    //Checks to see if our parameter overlaps any other appointments out there...
    boolean notOverlappingOthers(Appointment appointment) throws SQLException {
        ObservableList<Appointment> allAppointments = DBAppointment.getAllAppointments();
        for (Appointment currentAppointment : allAppointments){
            if (appointment.getStart().isBefore(currentAppointment.getEnd()) && appointment.getEnd().isAfter(currentAppointment.getStart())){
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

    @FXML private void cancelButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
        Parent parent = loader.load();
        Scene addProductPageScene = new Scene(parent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addProductPageScene);
    }

}
