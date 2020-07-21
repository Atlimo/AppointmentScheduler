package menus;

import dbData.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static dbData.DBData.*;

public class Reports implements Initializable {

    @FXML
    private ListView customerListView;
    @FXML
    private ListView typesListView;
    @FXML
    private ComboBox<String> usersCbox;
    @FXML
    private ListView<Appointment> usersListView;
    @FXML
    private Label reportTypeLbl;
    @FXML
    private AnchorPane mainPane;

    private Map<String, List<Appointment>> userSchedules;
    private ArrayList<String> userNamesList = new ArrayList<String>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showCustReport(new ActionEvent());//Types report will be the default report to show up

        ArrayList<Appointment> allAppointmentsList = new ArrayList(dbData.getReportsAppointmentList());
        allAppointmentsList.addAll(dbData.getAppointmentList()); //Add remaining appointments from current user.

        //Setup types report
        //Lambda expression provides an easy way to sum the values.
        Map<String, Integer> apptTypesMap = allAppointmentsList.stream().
                collect(Collectors.groupingBy(appointment -> appointment.getType(), Collectors.summingInt(num -> 1)));
        ObservableList<String> typesList = FXCollections.observableArrayList();
        apptTypesMap.entrySet().stream().forEach((typeMap -> typesList.add(typeMap.getKey() + ": "+typeMap.getValue())));
        typesListView.setItems(typesList);


        //Setup users report
        //Lambda provides an easy way to group appointments in lists by name.
        userSchedules = allAppointmentsList.stream().collect(Collectors.groupingBy( (Appointment appointment) ->
                        dbData.findUserByID(appointment.getUserID()).get().getUsername()));
        userSchedules.forEach((key, val) -> userNamesList.add(key));
        usersCbox.getItems().setAll(userNamesList);
        usersCbox.getSelectionModel().selectFirst();
        if(userSchedules.size() > 0)
            usersListView.setItems(FXCollections.observableList(userSchedules.get(usersCbox.getSelectionModel()
                    .getSelectedItem())));


        //Setup customers report
        //Lambda expression provides an easy way to sum the values.
        Map<String, Integer> customerMap = dbData.getCustomerList().stream().
                collect(Collectors.groupingBy(customer -> customer.getCityName(), Collectors.summingInt(num -> 1)));
        ObservableList<String> customerList = FXCollections.observableArrayList();
        customerMap.entrySet().stream().forEach((typeMap -> customerList.add(typeMap.getKey() + ": "+typeMap.getValue())));
        customerListView.setItems(customerList);
    }

    /**
     * Displays the types report.
     * @param event
     */
    @FXML
    private void showTypesReport(ActionEvent event) {
        reportTypeLbl.setText("Appointment Types Report");
        usersCbox.setVisible(false);
        usersListView.setVisible(false);
        customerListView.setVisible(false);
        typesListView.setVisible(true);
    }

    /**
     * Displays the user report.
     * @param event
     */
    @FXML
    private void showUserReport(ActionEvent event) {
        reportTypeLbl.setText("Users Report");
        customerListView.setVisible(false);
        typesListView.setVisible(false);
        usersCbox.setVisible(true);
        usersListView.setVisible(true);
        updateUsers(new ActionEvent());
    }

    /**
     * Displays the customer report.
     * @param event
     */
    @FXML
    private void showCustReport(ActionEvent event) {
        reportTypeLbl.setText("Customers Per City Report");
        usersCbox.setVisible(false);
        usersListView.setVisible(false);
        typesListView.setVisible(false);
        customerListView.setVisible(true);
    }


    @FXML
    private void openMainMenu(ActionEvent actionEvent) {
        MainMenu.openMainMenu(mainPane);
    }

    /**
     * Updates the ListView with the newly selected user data.
     * @param event
     */
    public void updateUsers(ActionEvent event) {
        if(userNamesList.size() > 0 && !usersCbox.getSelectionModel().isEmpty()) {

            usersListView.setItems(FXCollections.observableList(userSchedules.get(usersCbox.getSelectionModel()
                    .getSelectedItem())));
        }
    }
}
