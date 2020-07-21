package menus;

import dbData.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import static dataIO.DBConnector.*;
import static dbData.DBData.*;

public class CustomerRecords implements Initializable {

    @FXML
    private GridPane addGridPane; //Gridpane for adding new a customer.
    @FXML
    private GridPane updateGridPane; //Gridpane for updating and deleting a customer.
    @FXML
    private TableColumn<Customer, String> nameCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer, String> address2Col;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableColumn<Customer, String> cityCol;
    @FXML
    private TableColumn<Customer, String> countryCol;
    @FXML
    private TableColumn<Customer, String> postalCol;
    @FXML
    private TableView<Customer> custTableView;
    @FXML
    private TextField countryTextField;
    @FXML
    private TextField postalTextField;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private TextField address1TextField;
    @FXML
    private TextField address2TextField;
    @FXML
    private TextField cityTextField;
    @FXML
    private AnchorPane mainPane;

    //Errors to be printed when fields are left blank.
    public enum BlankErrors {
        NAME("The customer name is missing."),
        ADDRESS("The customer address is missing."),
        PHONE("The customer phone number is missing."),
        CITY("The customer city is missing."),
        COUNTRY("The customer country is missing."),
        POSTAL("The customer postal code is missing."),
        NONE("None");

        private final String alert;

        BlankErrors(String alert){
            this.alert = alert;
        }
    }

    public enum FieldLength{
        NAME("name", 45),
        ADDRESS1("address field 1", 50),
        ADDRESS2("address field 2", 50),
        PHONE("phone number", 20),
        CITY("city name", 50),
        COUNTRY("country name", 50),
        POSTAL("postal code", 10),
        NONE("none", 0);

        private final String name;
        private final int length;

        FieldLength(String name, int length){
            this.name = name;
            this.length = length;
        }

    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        custTableView.setItems(dbData.getCustomerList());
        if(!custTableView.getItems().isEmpty()) {
            custTableView.getSelectionModel().select(0);
            updateFieldData();
        }

        if(dbData.getAppointmentList().size() > 0) {
            custTableView.setItems(dbData.getCustomerList());
            custTableView.getSelectionModel().selectFirst();
            updateFieldData();
        }

        //Lambda expression here is very easy to read.
        nameCol.setCellValueFactory(cellData -> cellData.getValue().custNamePropProperty());
        addressCol.setCellValueFactory(cellData -> cellData.getValue().address1PropProperty());
        address2Col.setCellValueFactory(cellData -> cellData.getValue().address2PropProperty());
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phonePropProperty());
        cityCol.setCellValueFactory(cellData -> cellData.getValue().cityNamePropProperty());
        countryCol.setCellValueFactory(cellData -> cellData.getValue().countryNamePropProperty());
        postalCol.setCellValueFactory(cellData -> cellData.getValue().postalPropProperty());
    }


    /**
     * Updates the current selected customer information in the list and database.
     * @param actionEvent
     */
    @FXML
    private void updateCustomer(ActionEvent actionEvent) {
        if(isInformationFilledOut() && isCustomerSelected() && isFieldLengthCorrect()){
            editCustomerData(custTableView.getSelectionModel().getSelectedItem());
            int selectedItem = custTableView.getSelectionModel().getSelectedIndex();
            custTableView.setItems(FXCollections.observableArrayList());
            custTableView.setItems(dbData.getCustomerList());
            custTableView.getSelectionModel().select(selectedItem);
            Alert alert = new Alert(Alert.AlertType.INFORMATION, custTableView.getSelectionModel().getSelectedItem().getCustName()
                    + "'s information has been updated.");
            alert.setTitle("Customer Updated");
            alert.setHeaderText("Customer Information Has Been Updated");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent()) ;
        }
    }

    /**
     * Sets up the fields for inputtng a new customer.
     * @param actionEvent
     */
    @FXML
    private void createNewCust(ActionEvent actionEvent) {
        addGridPane.setVisible(true);
        updateGridPane.setVisible(false);

        nameTextField.setText("");
        address1TextField.setText("");
        address2TextField.setText("");
        phoneTextField.setText("");
        postalTextField.setText("");
        cityTextField.setText("");
        countryTextField.setText("");
    }

    /**
     * Adds a new customer to the customer list and database.
     */
    @FXML
    private void addCustomer(){
        if(isInformationFilledOut() && isFieldLengthCorrect()) {
            Customer newCust = new Customer();
            dbData.addCustomerToList(newCust);
            editCustomerData(newCust);
            custTableView.getSelectionModel().selectLast();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, custTableView.getSelectionModel().getSelectedItem().getCustName()
                    + " has been added.");
            alert.setTitle("Customer Added");
            alert.setHeaderText("A New Customer Has Been Added");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent()) ;

            addGridPane.setVisible(false);
            updateGridPane.setVisible(true);
        }
    }

    /**
     * Sets customer data from input fields.
     */
    private void editCustomerData(Customer customer){
        customer.setCountryName(countryTextField.getText());
        customer.setCityName(cityTextField.getText());
        customer.setAddress(address1TextField.getText(), address2TextField.getText(), postalTextField.getText(),
                phoneTextField.getText());
        customer.setCustName(nameTextField.getText());

        try {
            dbConnector.insertUpdateCountry(new Country(customer.getCountryID(), customer.getCountryName()));
            dbConnector.insertUpdateCity(new City(customer.getCityID(), customer.getCityName(),
                    customer.getCountryID()));
            dbConnector.insertUpdateAddress(new Address(customer.getAddressId(), customer.getAddress1(),
                    customer.getAddress2(), customer.getCityID(), customer.getPostalCode(),
                    customer.getPhoneNum()));
            dbConnector.insertUpdateCustomer(customer);

        }catch(SQLException e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "SQL Error: " + e.getMessage());
            alert.setTitle("Customer Update Error");
            alert.setHeaderText("Customer Update Could Not Be Performed");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent()) ;
        }
    }

    /**
     * Removes a customer from the customer list and database.
     * @param actionEvent
     */
    public void deleteCustomer(ActionEvent actionEvent) {
        if(!custTableView.getSelectionModel().isEmpty()) {
            Alert confirmDeletion = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " +
                    custTableView.getSelectionModel().getSelectedItem().getCustName() + "?", ButtonType.OK, ButtonType.CANCEL);
            confirmDeletion.setTitle("Deletion Confirmation");
            confirmDeletion.setHeaderText(custTableView.getSelectionModel().getSelectedItem().getCustName() +
                    " will be deleted");
            confirmDeletion.showAndWait();

            if (confirmDeletion.getResult() == ButtonType.OK && isCustomerSelected()) {
                try {
                    dbConnector.deleteCustomer(custTableView.getSelectionModel().getSelectedItem());
                    custTableView.getItems().remove(custTableView.getSelectionModel().getSelectedItem());
                    custTableView.getSelectionModel().selectFirst();
                    updateFieldData();
                } catch (SQLException | NullPointerException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "SQL Error: " + e.getMessage());
                    alert.setTitle("Customer Deletion Error");
                    alert.setHeaderText("Customer deletion could not be performed");
                    Optional<ButtonType> okayPressed = alert.showAndWait();
                    while (!okayPressed.isPresent()) ;
                }
            }
        }
    }

    @FXML
    private void openMainMenu(ActionEvent actionEvent) {
        MainMenu.openMainMenu(mainPane);
    }

    /**
     * Update customer information fields.
     */
    public void updateFieldData(){
        if(!custTableView.getSelectionModel().isEmpty()) {
            nameTextField.setText(custTableView.getSelectionModel().getSelectedItem().getCustName());
            address1TextField.setText(custTableView.getSelectionModel().getSelectedItem().getAddress1());
            address2TextField.setText(custTableView.getSelectionModel().getSelectedItem().getAddress2());
            phoneTextField.setText(custTableView.getSelectionModel().getSelectedItem().getPhoneNum());
            postalTextField.setText(custTableView.getSelectionModel().getSelectedItem().getPostalCode());
            cityTextField.setText(custTableView.getSelectionModel().getSelectedItem().getCityName());
            countryTextField.setText(custTableView.getSelectionModel().getSelectedItem().getCountryName());
        }else{
            nameTextField.setText("");
            address1TextField.setText("");
            address2TextField.setText("");
            phoneTextField.setText("");
            postalTextField.setText("");
            cityTextField.setText("");
            countryTextField.setText("");
        }
    }

    /**
     * Checks to see if all fields are filled out and gives an alert based on which one is not filled out.
     * @return
     */
    private boolean isInformationFilledOut(){
        BlankErrors blankError = BlankErrors.NONE;
        if(nameTextField.getText().equals(""))
            blankError = BlankErrors.NAME;
        else if(address1TextField.getText().equals(""))
            blankError = BlankErrors.ADDRESS;
        else if(phoneTextField.getText().equals(""))
            blankError = BlankErrors.PHONE;
        else if(postalTextField.getText().equals(""))
            blankError = BlankErrors.POSTAL;
        else if(cityTextField.getText().equals(""))
            blankError = BlankErrors.CITY;
        else if(countryTextField.getText().equals(""))
            blankError = BlankErrors.COUNTRY;


        if(!blankError.equals(BlankErrors.NONE)){
            Alert alert = new Alert(Alert.AlertType.ERROR, blankError.alert+" Please fill out the rest of the" +
                    " customer information before submitting.");
            alert.setTitle("Customer Records Error");
            alert.setHeaderText("Customer information missing.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent());
            return false;
        }

        return true;
    }

    /**
     * Checks to make sure the field lenths are not too large.
     * @return
     */
    private boolean isFieldLengthCorrect(){
        FieldLength fieldLengthError = FieldLength.NONE;
        if(nameTextField.getText().length() > FieldLength.NAME.length)
            fieldLengthError = FieldLength.NAME;
        else if(address1TextField.getText().length() > FieldLength.ADDRESS1.length)
            fieldLengthError = FieldLength.ADDRESS1;
        else if(address2TextField.getText().length() > FieldLength.ADDRESS2.length)
            fieldLengthError = FieldLength.ADDRESS2;
        else if(phoneTextField.getText().length() > FieldLength.PHONE.length)
            fieldLengthError = FieldLength.PHONE;
        else if(postalTextField.getText().length() > FieldLength.POSTAL.length)
            fieldLengthError = FieldLength.POSTAL;
        else if(cityTextField.getText().length() > FieldLength.CITY.length)
            fieldLengthError = FieldLength.CITY;
        else if(countryTextField.getText().length() > FieldLength.COUNTRY.length)
            fieldLengthError = FieldLength.COUNTRY;

        System.out.println(postalTextField.getText().length() > FieldLength.POSTAL.length);
        System.out.println(fieldLengthError.name);
        System.out.println(!fieldLengthError.equals(FieldLength.NONE));

        if(!fieldLengthError.equals(FieldLength.NONE)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "The customer " + fieldLengthError.name + " given is too long.");
            alert.setTitle("Customer Records Error");
            alert.setHeaderText("Postal code error.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent());
            return false;
        }
        return true;
    }

    /**
     * Outputs an alert if nocustomer is selceted.
     * @return
     */
    private boolean isCustomerSelected(){
        if(custTableView.getSelectionModel().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer before attempting to" +
                    " make changes.");
            alert.setTitle("Customer Records Error");
            alert.setHeaderText("No customer selected.");
            Optional<ButtonType> okayPressed = alert.showAndWait();
            while (!okayPressed.isPresent());
            return false;
        }
        return true;
    }

    @FXML
    private void loadCustomer(MouseEvent mouseEvent) {
        updateFieldData();
    }

    @FXML
    private void cancel(ActionEvent event) {
        addGridPane.setVisible(false);
        updateGridPane.setVisible(true);
        updateFieldData();
    }
}
