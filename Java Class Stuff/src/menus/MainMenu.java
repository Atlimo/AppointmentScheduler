package menus;

import dataIO.DBConnector;
import dbData.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static dataIO.DBConnector.getLoggedInUser;
import static dbData.DBData.*;

public class MainMenu implements Initializable {

    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> custIDColumn;
    @FXML
    private TableColumn<Appointment, String> startDateColumn;
    @FXML
    private TableColumn<Appointment, String> endDateColumn;
    @FXML
    private TableColumn<Appointment, String> typeColumn;
    @FXML
    private TableView<Appointment> apptTableView;
    @FXML
    private AnchorPane mainPane;

    private Predicate<Appointment> checkMonthly = (appt) ->
            appt.getStartDate().toLocalDateTime().isBefore(LocalDateTime.now().plusMonths(1));
    private Predicate<Appointment> checkWeekly = (appt) ->
            appt.getStartDate().toLocalDateTime().isBefore(LocalDateTime.now().plusWeeks(1));

    private ObservableList<Appointment> monthlyList;
    private ObservableList<Appointment> weeklyList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthlyList = FXCollections.observableList(dbData.getAppointmentList().stream().filter(checkMonthly)
                .collect(Collectors.toCollection(ArrayList::new)));

        weeklyList = FXCollections.observableList(dbData.getAppointmentList().stream().filter(checkWeekly)
                .collect(Collectors.toCollection(ArrayList::new)));


        apptTableView.setItems(dbData.getAppointmentList());
        custIDColumn.setCellValueFactory(cellData -> cellData.getValue().custIDPropProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().datePropProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDatePropProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDatePropProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typePropProperty());

        System.out.println(apptTableView.getItems().size());
    }

    public void openCustRecords(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menus/menusFXML/CustomerRecords.fxml"));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            CustomerRecords controller = loader.<CustomerRecords>getController();
            loader.setController(controller);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openApntsMenu(ActionEvent actionEvent) {
        if(dbData.getCustomerList().size() > 0){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/menus/menusFXML/Appointments.fxml"));
                Stage stage = (Stage) mainPane.getScene().getWindow();
                stage.setScene( new Scene(loader.load()));
                Appointments controller = loader.<Appointments>getController();
                loader.setController(controller);
                stage.show();
            }catch (IOException e){
                e.printStackTrace();
            }
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Please add a customer before setting an " +
                    "appointment.");
            alert.setTitle("No Customers");
            alert.setHeaderText("There are no customers to schedule for.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent());
        }
    }

    public void openReportsMenu(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menus/menusFXML/Reports.fxml"));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            Reports controller = loader.<Reports>getController();
            loader.setController(controller);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void openLoginMenu(ActionEvent actionEvent) {
        try {
            getLoggedInUser().reset();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Lang", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/menus/menusFXML/LoginScreen.fxml"), resourceBundle);
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            Login controller = loader.<Login>getController();
            loader.setController(controller);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showAllAppointments(ActionEvent event) {
        apptTableView.setItems(dbData.getAppointmentList());
    }

    @FXML
    public void showMonthlyAppointments(ActionEvent event) {
        apptTableView.setItems(monthlyList);
    }

    @FXML
    public void showWeeklyAppointments(ActionEvent event) {
        apptTableView.setItems(weeklyList);
    }

    public static void openMainMenu(Pane mainPane){
        try {
            FXMLLoader loader = new FXMLLoader(new MainMenu().getClass()
                    .getResource("/menus/menusFXML/MainMenu.fxml"));
            Stage stage = (Stage) mainPane.getScene().getWindow();
            stage.setScene( new Scene(loader.load()));
            MainMenu controller = loader.<MainMenu>getController();
            loader.setController(controller);
            stage.show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
