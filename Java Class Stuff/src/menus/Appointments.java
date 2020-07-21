package menus;

import dbData.Appointment;
import dbData.Customer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;

import static dataIO.DBConnector.*;
import static dbData.DBData.dbData;
import static javafx.scene.control.SpinnerValueFactory.*;

public class Appointments implements Initializable {

    @FXML
    private HBox updateHBox;
    @FXML
    private Spinner<Integer> endHourSpin;
    @FXML
    private Spinner<Integer> endMinSpin;
    @FXML
    private HBox addHBox;
    @FXML
    private Spinner<Integer> startHourSpin;
    @FXML
    private Spinner<Integer> startMinSpin;
    @FXML
    private Label apptTimeLbl;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField typeTextField;
    @FXML
    private ComboBox<Customer> custCbox;
    @FXML
    private AnchorPane mainPane;

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

    private static final LocalTime START_BUSINESS_HOURS = LocalTime.of(8, 0);
    private static final LocalTime END_BUSINESS_HOURS = LocalTime.of(16, 30);

    private static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        custCbox.setItems(dbData.getCustomerList());
        if(!custCbox.getItems().isEmpty()) {
            custCbox.getSelectionModel().select(0);
            selectCustomer();
        }

        datePicker.setDayCellFactory(Appointments::getDatePicker);
        datePicker.setValue(LocalDate.now());
        apptTimeLbl.setText("00:00");

        updateFields(new ActionEvent());

        //Setup date spinners
        startHourSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 23, LocalDateTime.now()
                .getHour()));
        startMinSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 59, LocalDateTime
                .now().getMinute()+1));
        endHourSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 3, 0));
        endMinSpin.valueFactoryProperty().set(new IntegerSpinnerValueFactory(0, 59, 0));

        //Add action listener to update the end time label to all spinners
        //Lambda expression here is very easy to read.
        startHourSpin.valueProperty().addListener((obs, oldValue, newValue) -> updateEndTime());
        startMinSpin.valueProperty().addListener((obs, oldValue, newValue) -> updateEndTime());
        endHourSpin.valueProperty().addListener((obs, oldValue, newValue) -> updateEndTime());
        endMinSpin.valueProperty().addListener((obs, oldValue, newValue) -> updateEndTime());
        updateEndTime();

        //Setup appointment table
        apptTableView.setItems(dbData.getAppointmentList());
        if(dbData.getAppointmentList().size() > 0) {
            apptTableView.getSelectionModel().selectFirst();
            updateFields(new ActionEvent());
        }
        //Lambda expression here is very easy to read.
        custIDColumn.setCellValueFactory(cellData -> cellData.getValue().custIDPropProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().datePropProperty());
        startDateColumn.setCellValueFactory(cellData -> cellData.getValue().startDatePropProperty());
        endDateColumn.setCellValueFactory(cellData -> cellData.getValue().endDatePropProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typePropProperty());
    }

    @FXML
    private void openMainMenu(ActionEvent actionEvent) {
        MainMenu.openMainMenu(mainPane);
    }

    private static DateCell getDatePicker(DatePicker picker) {
        return new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        };
    }

    /**
     * Checks for time conflicts between the two appointments
     * @param updatingAppt
     * @return
     */
    private Optional<Appointment> checkTimeConflicts(boolean updatingAppt){
        for(Appointment appointments : dbData.getAppointmentList()){
            if(updatingAppt && appointments.getAppointmentID() == apptTableView.getSelectionModel().getSelectedItem()
                    .getAppointmentID())
                continue;
            else if(isStartTimeDuring(appointments) || isEndTimeDuring(appointments)
                    || isApptBetweenStartEnd(appointments))
                return Optional.of(appointments);
        }
        return Optional.empty();
    }


    @FXML
    private void updateFields(ActionEvent actionEvent) {
        if(apptTableView.getItems().size() > 0 && !apptTableView.getSelectionModel().isEmpty()) {
            Appointment selectedAppt = apptTableView.getSelectionModel().getSelectedItem();
            datePicker.setValue(selectedAppt.getStartDate().toLocalDateTime()
                    .toLocalDate());

            typeTextField.setText(selectedAppt.getType());

            LocalDateTime startTime = selectedAppt.getStartDate().toLocalDateTime();
            LocalDateTime endTime = selectedAppt.getEndDate().toLocalDateTime();

            startHourSpin.getValueFactory().setValue(startTime.getHour());
            startMinSpin.getValueFactory().setValue(startTime.getMinute());

            int hours = (int) startTime.until( endTime, ChronoUnit.HOURS );
            int minutes = (int) startTime.until( endTime, ChronoUnit.MINUTES ) - hours*60;

            endHourSpin.getValueFactory().setValue(hours);
            endMinSpin.getValueFactory().setValue(minutes);

            apptTimeLbl.setText(getEndTime().toLocalDateTime().format(timeFormatter));

            updateEndTime();
            selectCustomer();
        }
    }

    private void selectCustomer(){
        if(apptTableView.getItems().size() <= 0)
            return;
        for(Customer customers : dbData.getCustomerList()){
            if(apptTableView.getSelectionModel().getSelectedItem().getCustID() == customers.getCustID()){
                custCbox.getSelectionModel().select(customers);
                break;
            }
        }
    }

    private Timestamp getStartTime(){
        return Timestamp.valueOf(LocalDateTime.of(datePicker.getValue(),
                LocalTime.of(startHourSpin.getValue(), startMinSpin.getValue())));
    }

    private Timestamp getEndTime(){
        return Timestamp.valueOf(getStartTime().toLocalDateTime().plusHours(endHourSpin.getValue())
                .plusMinutes(endMinSpin.getValue()));
    }

    @FXML
    private void addAppointment(ActionEvent actionEvent) {
        Optional<Appointment> conflictAppt = checkTimeConflicts(false);
        if(conflictAppt.isPresent()){
            scheduleConflictAlert(conflictAppt.get());
        }
        else if(!isWithinBusinessHours()){
            businessHoursAlert();
        }
        else if(typeUnset()){
            typeUnsetAlert();
        }
        else if(isApptBeforeCurrentTime()){
            apptBeforeCurrentTime();
        }
        else{
            Appointment appointment = new Appointment();
            appointment.setStartDate(getStartTime());
            appointment.setEndDate(getEndTime());
            appointment.setType(typeTextField.getText());
            appointment.setUserID(dbConnector.getLoggedInUser().getUserID());
            appointment.createNewID();
            appointment.setCustID(custCbox.getSelectionModel().getSelectedItem().getCustID());

            try {
                dbConnector.insertAppointment(appointment);
                apptTableView.getItems().add(appointment);
                apptTableView.getSelectionModel().selectLast();

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "A new appointment has been " +
                        "scheluded for " +custCbox.getSelectionModel().getSelectedItem() + ".");
                alert.setTitle("Appointment Added");
                alert.setHeaderText("Your appointment has been scheduled.");
                Optional<ButtonType> okayPressed = alert.showAndWait();
                while (!okayPressed.isPresent()) ;
                updateHBox.setVisible(true);
                addHBox.setVisible(false);
                apptTableView.setDisable(false);

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error: "+e.getMessage());
                alert.setTitle("New Appointment Error");
                alert.setHeaderText("A New Appointment Could Not Be Created.");
                Optional<ButtonType> okayPressed = alert.showAndWait();
                while (!okayPressed.isPresent()) ;
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void updateEndTime() {
        if(appointmentTooShort())
            endMinSpin.getValueFactory().setValue(5);
        apptTimeLbl.setText(getEndTime().toLocalDateTime().format(timeFormatter));
    }

    @FXML
    private void updateAppointment(ActionEvent actionEvent) {
        if(!apptTableView.getSelectionModel().isEmpty()) {
            Optional<Appointment> conflictAppt = checkTimeConflicts(true);
            if (conflictAppt.isPresent()) {
                scheduleConflictAlert(conflictAppt.get());
            } else if (!isWithinBusinessHours()) {
                businessHoursAlert();
            } else if (typeUnset()) {
                typeUnsetAlert();
            }
            else if(isApptBeforeCurrentTime()){
                apptBeforeCurrentTime();
            }
            else {
                Appointment appointment = apptTableView.getSelectionModel().getSelectedItem();
                appointment.setStartDate(getStartTime());
                appointment.setEndDate(getEndTime());
                appointment.setType(typeTextField.getText());
                appointment.setCustID(custCbox.getSelectionModel().getSelectedItem().getCustID());

                try {
                    dbConnector.updateAppointment(appointment);

                    int selectedItem = apptTableView.getSelectionModel().getSelectedIndex();
                    apptTableView.setItems(FXCollections.observableArrayList());
                    apptTableView.setItems(dbData.getAppointmentList());
                    apptTableView.getSelectionModel().select(selectedItem);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Your appointment has been updated.");
                    alert.setTitle("Appointment Updated");
                    alert.setHeaderText("Your appointment has been updated.");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (!okayPressed.isPresent()) ;

                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
                    alert.setTitle("Update Appointment Error");
                    alert.setHeaderText("Appointment Could Not Be Updated.");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (!okayPressed.isPresent()) ;
                    e.printStackTrace();
                }
            }
        }
    }

    private void businessHoursAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appoint time is outside of business" +
                " hours. Please select another time.");
        alert.setTitle("Schedule Conflict");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (!okayPressed.isPresent()) ;
    }

    private void scheduleConflictAlert(Appointment appointment) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "There was a scheduling conflict at " +
                appointment.getStartDate() + ". Please select another time.");
        alert.setTitle("Schedule Conflict");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (!okayPressed.isPresent()) ;
    }

    private void typeUnsetAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment type is not set.");
        alert.setTitle("Missing Information");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (!okayPressed.isPresent()) ;
    }

    private void apptBeforeCurrentTime() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "The appointment time has been set before the" +
                " current time. Please choose another time.");
        alert.setTitle("Schedule Conflict");
        alert.setHeaderText("Appointment Could Not Be Made.");
        Optional<ButtonType> okayPressed = alert.showAndWait();
        while (!okayPressed.isPresent()) ;
    }

    @FXML
    private void deleteAppointment(ActionEvent event){
        if(!apptTableView.getSelectionModel().isEmpty()) {
            Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete" +
                    "appointment starting at " + apptTableView.getSelectionModel().getSelectedItem().getStartDate() + "?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirmDeletion.setTitle("Deletion Confirmation");
            confirmDeletion.setHeaderText(apptTableView.getSelectionModel().getSelectedItem() + " will be deleted");
            confirmDeletion.showAndWait();

            if (confirmDeletion.getResult() == ButtonType.OK) {
                try {
                    dbConnector.deleteAppointment(apptTableView.getSelectionModel().getSelectedItem());
                    apptTableView.getItems().remove(apptTableView.getSelectionModel().getSelectedItem());

                    apptTableView.getSelectionModel().selectFirst();
                    updateFields(new ActionEvent());

                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
                    alert.setTitle("Update Deletion Error");
                    alert.setHeaderText("Appointment Could Not Be Deleted.");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (!okayPressed.isPresent()) ;
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Checks if the chosen start time is during the given appointment
     * @param appointment
     * @return
     */
    private boolean isStartTimeDuring(Appointment appointment){
        return !getStartTime().before(appointment.getStartDate()) && !getStartTime().after(appointment.getEndDate());
    }


    /**
     * Checks if the chosen end time is during the given appointment
     * @param appointment
     * @return
     */
    private boolean isEndTimeDuring(Appointment appointment){
        return !getEndTime().before(appointment.getStartDate()) && !getEndTime().after(appointment.getEndDate());
    }


    /**
     * Checks if the chosen start and end times encompass the given appointment
     * @param appointment
     * @return
     */
    private boolean isApptBetweenStartEnd(Appointment appointment){
        return getStartTime().before(appointment.getStartDate()) && getEndTime().after(appointment.getEndDate());
    }

    /**
     * Checks if the appointment is during business hours.
      * @return
     */
    boolean isWithinBusinessHours(){
        return !getStartTime().toLocalDateTime().toLocalTime().isBefore(START_BUSINESS_HOURS)
                && !getStartTime().toLocalDateTime().toLocalTime().isAfter(END_BUSINESS_HOURS)
                && !getEndTime().toLocalDateTime().toLocalTime().isBefore(START_BUSINESS_HOURS)
                && !getEndTime().toLocalDateTime().toLocalTime().isAfter(END_BUSINESS_HOURS);
    }

    /**
     * Checks to make sure the appointment time is at least 5 minutes.
     * @return
     */
    private boolean appointmentTooShort(){
        return endHourSpin.getValue() * 60 + endMinSpin.getValue() < 5;
    }

    /**
     * Checks if the appointment type is blank.
     * @return
     */
    private boolean typeUnset(){
        return typeTextField.getText().trim().equalsIgnoreCase("");
    }

    /**
     * Checks if the appoinment is schedules before the current time.
     * @return
     */
    private boolean isApptBeforeCurrentTime(){
        return!getStartTime().after(Timestamp.valueOf(LocalDateTime.now()));
    }

    public void loadAppointment(MouseEvent mouseEvent) {
        updateFields(new ActionEvent());
    }

    public void cancel(ActionEvent event) {
        updateHBox.setVisible(true);
        addHBox.setVisible(false);
        apptTableView.setDisable(false);
        updateFields(new ActionEvent());
    }

    /**
     * Sets up the page for adding a new appointmnet.
     * @param event
     */
    public void showAddMenu(ActionEvent event) {
        apptTableView.setDisable(true);
        updateHBox.setVisible(false);
        addHBox.setVisible(true);

        startHourSpin.getValueFactory().setValue(LocalDateTime.now().getHour());
        startMinSpin.getValueFactory().setValue(LocalDateTime.now().getMinute());
        endHourSpin.getValueFactory().setValue(0);
        endMinSpin.getValueFactory().setValue(0);

        datePicker.setValue(LocalDate.now());
        apptTimeLbl.setText("00:00");
        typeTextField.setText("");
    }
}
