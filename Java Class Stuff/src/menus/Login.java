package menus;

import dataIO.DBConnector;
import dbData.Appointment;
import dataIO.UserLogs;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static dataIO.DBConnector.dbConnector;
import static dbData.DBData.dbData;


public class Login implements Initializable {

    @FXML
    private  TextField visiblePassField;
    @FXML
    private CheckBox showPassCheckbox;
    @FXML
    private PasswordField passField;
    @FXML
    private TextField usernameField;

    private Stage primaryStage;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void displayPassword(ActionEvent event){
        if(showPassCheckbox.isSelected()) {
            visiblePassField.setText(passField.getText());
            passField.setVisible(false);
            visiblePassField.setVisible(true);
        }
        else {
            passField.setText(visiblePassField.getText());
            passField.setVisible(true);
            visiblePassField.setVisible(false);
        }
    }

    public void login(ActionEvent actionEvent) {
        try {
            String password = passField.isVisible() ? passField.getText() : visiblePassField.getText();
            dbConnector.login(usernameField.getText(), password);
            dbConnector.setAppointments();
            if (dbConnector.getLoggedInUser().getUserID() != -1){
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/menus/menusFXML/MainMenu.fxml"));
                    Stage stage = (Stage) passField.getScene().getWindow();
                    stage.setScene( new Scene(loader.load()));
                    MainMenu controller = loader.<MainMenu>getController();
                    UserLogs.saveLoginInfo(dbConnector.getLoggedInUser());
                    loader.setController(controller);
                    stage.show();

                    //Warn User if appointment is coming up within 15 minutes
                    boolean appointmentSoon = false;
                    for(Appointment appointments : dbData.getAppointmentList()){
                        if(appointments.getStartDate().before(Timestamp.valueOf(LocalDateTime.now().plusMinutes(15)))){
                            appointmentSoon = true;
                            break;
                        }
                    }

                    if (appointmentSoon){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "You have an appointment coming up soon." +
                                "\nPlease check your appointments for more information");
                        alert.setTitle("Upcoming Appointment");
                        alert.setHeaderText("Appoint Within 15 Minutes");
                        Optional<ButtonType> okayPressed = alert.showAndWait();
                        while (!okayPressed.isPresent()) ;
                    }

                }catch (IOException e){
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, resourceBundle.getString("alertLoginContext"));
                    alert.setTitle(resourceBundle.getString("alertLoginIOETitle"));
                    alert.setHeaderText(resourceBundle.getString("alertLoginIOEMessage"));
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    e.printStackTrace();
                    while (!okayPressed.isPresent());
                }
            }else{
                ResourceBundle resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
                Alert alert = new Alert(Alert.AlertType.INFORMATION, resourceBundle.getString("alertUserPassContext"));
                alert.setTitle(resourceBundle.getString("alertUserPassTitle"));
                alert.setHeaderText(resourceBundle.getString("alertUserPassMessage"));
                Optional<ButtonType> okayPressed = alert.showAndWait();
                while (!okayPressed.isPresent());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: " + e);
        }
    }

    public void exitProgram(ActionEvent actionEvent) {
        DBConnector.dbConnector.closeConnection();
        Platform.exit();
        System.exit(0);
    }
}
