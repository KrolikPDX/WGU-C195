package Main;

import SchedulingApp.DAO.DBConnector;
import SchedulingApp.Model.Customer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Author: Joseph Demyanovskiy
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../SchedulingApp/View_Controller/LoginPage.fxml"));
        primaryStage.setTitle("C195");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {
        DBConnector.openConnection();
        launch(args); //Only launch program if we have successfully connected to DB
        DBConnector.closeConnection();
    }
}
