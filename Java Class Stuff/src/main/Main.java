package main;

import dataIO.DBConnector;
import dbData.DBData;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    public static void main(String[] args) {
        DBConnector.dbConnector.connectToDatabase();
        DBData.dbData.populateDbInfo(); //Loads the database information into lists.
        launch();
    }


    @Override
    public void start(Stage primaryStage) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/menus/menusFXML/LoginScreen.fxml"),
                resourceBundle);
        Parent root = loader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("Database Login");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(window -> {
            DBConnector.dbConnector.closeConnection();
            Platform.exit();
        });
    }
}
