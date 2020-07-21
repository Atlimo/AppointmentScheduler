package dbData;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static dataIO.DBConnector.*;

public class DBData {

    private static ArrayList<User> userList = new ArrayList<>();
    private static ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private static ObservableList<Appointment> currentUserApptsList = FXCollections.observableArrayList();
    private static ArrayList<Appointment> reportsAppointmentList = new ArrayList();
    private static ArrayList<City> cityList = new ArrayList<>();
    private static ArrayList<Country> countryList = new ArrayList<>();
    private static ArrayList<Address> addressList = new ArrayList<>();

    public static final DBData dbData = new DBData();

    private DBData(){}

    /**
     * Calls the databaes methods for setting all but the appointment lists with database data.
     */
    public void populateDbInfo(){
        try {
            dbConnector.setUsers();
            dbConnector.setCountries();
            dbConnector.setCities();
            dbConnector.setAddresses();
            dbConnector.setCustomers();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addCustomerToList(Customer customer){
        int maxID = 1;
        for(Customer customers : customerList){
            maxID = Math.max(maxID, customers.getCustID());
        }
        customer.setCustID(maxID+1);
        customerList.add(customer);
    }


    public Optional<Customer> findCustomerByID(int custID) throws NoSuchElementException {
        for(Customer customer : customerList){
            if(customer.getCustID() == custID)
                return Optional.of(customer);
        }
        return Optional.empty();
    }


    public Optional<User> findUserByID(int userID){
        for(User users : userList){
            if(users.getUserID() == userID) {
                return Optional.of(users);
            }
        }
        return Optional.empty();
    }


    public static ArrayList<User> getUserList() {
        return userList;
    }

    public ArrayList<Address> getAddressList() {
        return addressList;
    }

    public ArrayList<City> getCityList() {
        return cityList;
    }

    public ArrayList<Country> getCountryList() {
        return countryList;
    }

    public ObservableList<Customer> getCustomerList() {
        return customerList;
    }

    public ObservableList<Appointment> getAppointmentList() {
        return currentUserApptsList;
    }

    public static ArrayList<Appointment> getReportsAppointmentList() {
        return reportsAppointmentList;
    }
}
