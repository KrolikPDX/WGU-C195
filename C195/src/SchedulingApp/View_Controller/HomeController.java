package SchedulingApp.View_Controller;

import SchedulingApp.DAO.DBAppointment;
import SchedulingApp.Model.Appointment;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

//region TableViews for week and month appointments
    @FXML private TableView<Appointment> appointmentsThisWeekView;
    @FXML private TableColumn<Appointment, String> weekNameColumn;
    @FXML private TableColumn<Appointment, String> weekTitleColumn;
    @FXML private TableColumn<Appointment, String> weekDescriptionColumn;
    @FXML private TableColumn<Appointment, String> weekLocationColumn;
    @FXML private TableColumn<Appointment, String> weekContactColumn;
    @FXML private TableColumn<Appointment, String> weekTypeColumn;
    @FXML private TableColumn<Appointment, String> weekUrlColumn;
    @FXML private TableColumn<Appointment, String> weekStartColumn;
    @FXML private TableColumn<Appointment, String> weekEndColumn;

    @FXML private TableView<Appointment> appointmentsThisMonthView;
    @FXML private TableColumn<Appointment, String> monthNameColumn;
    @FXML private TableColumn<Appointment, String> monthTitleColumn;
    @FXML private TableColumn<Appointment, String> monthDescriptionColumn;
    @FXML private TableColumn<Appointment, String> monthLocationColumn;
    @FXML private TableColumn<Appointment, String> monthContactColumn;
    @FXML private TableColumn<Appointment, String> monthTypeColumn;
    @FXML private TableColumn<Appointment, String> monthUrlColumn;
    @FXML private TableColumn<Appointment, String> monthStartColumn;
    @FXML private TableColumn<Appointment, String> monthEndColumn;

    @FXML private TableView<Appointment> allAppointmentsTableView;
    @FXML private TableColumn<Appointment, String> allAppointmentsNameColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsTitleColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsDescriptionColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsLocationColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsContactColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsTypeColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsUrlColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsStartColumn;
    @FXML private TableColumn<Appointment, String> allAppointmentsEndColumn;
//endregion TableViews for week and month appointments
    @FXML private Tab weekTab;
    @FXML private Tab monthTab;
    @FXML private Tab allTab;

    private final DateTimeFormatter displayFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd   |   HH:mm");

    //Setup for TableViews
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        weekNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
        weekTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        weekDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        weekLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        weekContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        weekTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        weekUrlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        weekStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(displayFormat)));
        weekEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(displayFormat)));

        monthNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
        monthTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        monthDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        monthLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        monthContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        monthTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        monthUrlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        monthStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(displayFormat)));
        monthEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(displayFormat)));

        allAppointmentsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerName()));
        allAppointmentsTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        allAppointmentsDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        allAppointmentsLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        allAppointmentsContactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        allAppointmentsTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        allAppointmentsUrlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        allAppointmentsStartColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStart().format(displayFormat)));
        allAppointmentsEndColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEnd().format(displayFormat)));

        updateTables.run();

    }

    //Go to addCustomerPage
    @FXML private void addCustomerClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/AddCustomerPage.fxml"));
        Parent parent = loader.load();
        Scene addCustomerScene = new Scene(parent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addCustomerScene);
    }

    //Go to modifyCustomerPage
    @FXML private void modifyCustomerClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/ModifyCustomerPage.fxml"));
        Parent parent = loader.load();
        Scene addCustomerScene = new Scene(parent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addCustomerScene);

    }

    //Go to deleteCustomerPage
    @FXML private void deleteCustomerClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/DeleteCustomerPage.fxml"));
        Parent parent = loader.load();
        Scene addCustomerScene = new Scene(parent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addCustomerScene);
    }

    //Go to addAppointmentPage
    @FXML private void addAppointmentClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/AddAppointmentPage.fxml"));
        Parent parent = loader.load();
        Scene addCustomerScene = new Scene(parent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addCustomerScene);
    }

    //Go to modifyAppointmentPage
    @FXML private void modifyAppointmentClicked(ActionEvent event) throws IOException {
        Appointment weekAppointment = appointmentsThisWeekView.getSelectionModel().getSelectedItem();
        Appointment monthAppointment = appointmentsThisMonthView.getSelectionModel().getSelectedItem();
        Appointment appointment = allAppointmentsTableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/ModifyAppointmentPage.fxml"));
        Parent parent = loader.load();
        Scene addCustomerScene = new Scene(parent);
        ModifyAppointmentController controller = loader.getController();

        if (weekAppointment != null ){
            controller.initAppointment(weekAppointment); //Null for now until we setup table to pick an appointment
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addCustomerScene);
        }
        else if (monthAppointment != null){
            controller.initAppointment(monthAppointment); //Null for now until we setup table to pick an appointment
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addCustomerScene);
        }
        else if (appointment != null){
            controller.initAppointment(appointment); //Null for now until we setup table to pick an appointment
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addCustomerScene);
        }
    }

    //Used to show alerts inside delete appointment
    interface AlertPopup{ boolean show();}
    private final Runnable updateTables = () -> {
        try {
            appointmentsThisWeekView.setItems(DBAppointment.getWeekAppointment());
            appointmentsThisMonthView.setItems(DBAppointment.getMonthAppointment());
            allAppointmentsTableView.setItems(DBAppointment.getAllAppointments());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    };
    @FXML private void deleteAppointmentClicked() throws SQLException {
        //Lamda used to simplify alert popup
        AlertPopup alertPopup = () -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("CONFIRM APPOINTMENT DELETE");
            alert.setHeaderText("Are you sure you want to delete this appointment? \nThis cannot be undone!");
            alert.setContentText("Press OK to delete.");
            alert.showAndWait();
            return (alert.getResult() == ButtonType.OK);
        };

        if (weekTab.isSelected()){
            Appointment appointment = appointmentsThisWeekView.getSelectionModel().getSelectedItem();
            if (appointment != null && alertPopup.show()){
                DBAppointment.deleteAppointmentInDB(appointment);
                updateTables.run();
            }
        }
        else if (monthTab.isSelected() && alertPopup.show()){
            Appointment appointment = appointmentsThisMonthView.getSelectionModel().getSelectedItem();
            if (appointment != null){
                DBAppointment.deleteAppointmentInDB(appointment);
                updateTables.run();
            }
        }
        else if (allTab.isSelected() && alertPopup.show()){
            Appointment appointment = allAppointmentsTableView.getSelectionModel().getSelectedItem();
            if (appointment != null) {
                DBAppointment.deleteAppointmentInDB(appointment);
                updateTables.run();
            }
        }
    }

    //Get user Logs by opening userlog.txt inside notepad.exe
    @FXML void getUserLogs(ActionEvent event) throws IOException {
        ProcessBuilder process = new ProcessBuilder("Notepad.exe", "userlog.txt");
        process.start();
    }

    //Go  to reportPage
    @FXML private void reportButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/ReportsPage.fxml"));
        Parent parent = loader.load();
        Scene addCustomerScene = new Scene(parent);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(addCustomerScene);
    }

    //Exit the program after confirmation
    @FXML private void exitButtonClicked(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("Press OK to exit.");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK)
            System.exit(0);
    }
}
