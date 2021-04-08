package SchedulingApp.View_Controller;

import SchedulingApp.DAO.DBAppointment;
import SchedulingApp.DAO.DBConnector;
import SchedulingApp.Model.Appointment;
import SchedulingApp.Model.User;
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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class LoginController implements Initializable {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button exitButton;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;


    public static User loggedUser = new User();
    Locale userLocale = Locale.getDefault();
    ResourceBundle resourceBundle;
    public static ZoneId zoneID = ZoneId.systemDefault();
    private Logger userLog = Logger.getLogger("userlog.txt");

    @Override public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Zone: " + zoneID.toString());
        System.out.println("with time: " + LocalTime.now(zoneID));
        this.resourceBundle = ResourceBundle.getBundle("LanguageResource/rb", userLocale);
        titleLabel.setText(resourceBundle.getString("title"));
        usernameLabel.setText(resourceBundle.getString("username") + ":");
        passwordLabel.setText(resourceBundle.getString("password") + ":");
        usernameField.setPromptText(resourceBundle.getString("usernamePrompt"));
        passwordField.setPromptText(resourceBundle.getString("passwordPrompt"));
        loginButton.setText(resourceBundle.getString("loginButtonText"));
        exitButton.setText(resourceBundle.getString("exitButtonText"));
    }

    //When we press loginButton
    @FXML private void loginButtonClicked(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        String username =  "'" + usernameField.getText().trim() + "'";
        String password = "'" + passwordField.getText().trim() + "'";

        //Validate that the user login attempt
        if (validateLogin(username, password)){
            //Store log into userlog.txt
            FileHandler userLogFH = new FileHandler("userlog.txt", true);
            SimpleFormatter sf = new SimpleFormatter();
            userLogFH.setFormatter(sf);
            userLog.addHandler(userLogFH);
            userLog.setLevel(Level.INFO);
            userLog.info("User '" + loggedUser.getUserName() + "' logged in with " + zoneID + " timezone");

            //Check if any appointments are within 15 minutes for the logged user
            getReminders();

            //Load HomePage
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/SchedulingApp/View_Controller/HomePage.fxml"));
            Parent parent = loader.load();
            Scene addProductPageScene = new Scene(parent);
            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
            window.setScene(addProductPageScene);
        }
        else{
            //We failed to login
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(resourceBundle.getString("loginFailedTitle"));
            alert.setHeaderText(resourceBundle.getString("loginFailedHeader"));
            alert.showAndWait();
        }
    }

    //Exit button pressed
    @FXML private void exitButtonClicked(ActionEvent event){
        //Confirm exit
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("exitTitle"));
        alert.setHeaderText(resourceBundle.getString("exitHeader"));
        alert.setContentText(resourceBundle.getString("exitContext"));
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK)
            System.exit(0);
    }

    //Validates login through DB
    public boolean validateLogin(String username, String password) throws ClassNotFoundException, SQLException {
        //Binary makes it case sensitive
        String sqlStatement = "SELECT * FROM user WHERE BINARY userName = " + username + " AND BINARY password = " + password;
        Statement statement = DBConnector.connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sqlStatement);
        if (resultSet.next()) {
            //If resultSet has something inside, it means we successfully entered correct information
            loggedUser.setUserName(resultSet.getString("userName"));
            loggedUser.setPassword(resultSet.getString("password"));
            loggedUser.setUserId(resultSet.getInt("userId"));
            return true;
        }
        return false;
    }

    private void getReminders() throws SQLException {
        LocalDateTime currentTime = LocalDateTime.now();
        ObservableList<Appointment> allAppointments = DBAppointment.getAppointmentsWithUserID(loggedUser.getUserId()); //Get all appointments of logged user
        for (Appointment currentAppointment : allAppointments){ //Go through every appointment
            //If our currentAppointment is after now but before 15 minutes from now...
            if (currentAppointment.getStart().isAfter(currentTime.atZone(zoneID)) && currentAppointment.getStart().isBefore(currentTime.plusMinutes(15).atZone(zoneID))){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming appointments");
                alert.setHeaderText("You have an appointment with " + currentAppointment.getCustomer().getCustomerName() + " within 15 minutes!");
                alert.showAndWait();
                return;
            }
        }


    }
}
