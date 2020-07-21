package dataIO;

import dbData.*;
import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static dbData.DBData.dbData;
import static utils.DateConversionFactory.*;

public class DBConnector {

    private static final String serverAddress = "3.227.166.251";
    private static final String dbName = "U06og1";
    private static final String dbUsername = "U06og1";
    private static final String dbPassword = "53688825281";
    private static final String dbURL = "jdbc:mysql://"+serverAddress+"/"+dbName;
    private static Connection dbConn;

    public static final String SELECT_USER_AND_PASSWORD = "SELECT * FROM user WHERE userName = ? AND password = ?;";
    public static final String SELECT_ALL_APPOINTMENTS = "SELECT * FROM appointment";
    public static final String SELECT_ALL_USERS = "SELECT * FROM user";
    public static final String SELECT_ALL_CITIES = "SELECT * FROM city";
    public static final String SELECT_ALL_COUNTRIES = "SELECT * FROM country";
    public static final String SELECT_ALL_ADDRESSES = "SELECT * FROM address";
    public static final String SELECT_CUSTOMER_INFORMATION = "select customerId, customerName, addressId, address," +
            " address2, postalCode, phone, cityId, city, countryId, country" +
            " from customer join address using (addressID) join city using (cityId) join country using (countryId)";


    public static final String INSERT_UPDATE_CUSTOMER = "INSERT INTO customer VALUES (?, ?, ?, ?, ?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE customerID = ?, customerName = ?, addressId = ?, lastUpdate = ?, lastUpdateBy = ?";
    public static final String INSERT_UPDATE_ADDRESS = "INSERT INTO address VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE addressID = ?, address = ?, address2 = ?, cityID = ?, postalCode = ?, phone = ?," +
            " lastUpdate = ?, lastUpdateBy = ?";
    public static final String INSERT_UPDATE_CITY = "INSERT INTO city VALUES(?, ?, ?, ?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE cityID = ?, city = ?, countryId = ?, lastUpdate = ?, lastUpdateBy = ?";
    public static final String INSERT_UPDATE_COUNTRY = "INSERT INTO country VALUES(?, ?, ?, ?, ?, ?)" +
            " ON DUPLICATE KEY UPDATE countryID = ?, country = ?, lastUpdate = ?, lastUpdateBy = ?";
    public static final String UPDATE_APPOINTMENT = "UPDATE appointment set customerId = ?, type = ?, start = ?," +
            " end = ?, lastUpdate = ?, lastUpdateBy = ? WHERE appointmentId = ?";


    public static final String DELETE_CUSTOMER = "DELETE FROM customer where customerId = ?";
    public static final String DELETE_ADDRESS = "DELETE FROM address where addressId = ? and addressId not in" +
            " (SELECT  addressId FROM customer)";
    public static final String DELETE_APPOINTMENT = "DELETE FROM appointment where appointmentId = ?";


    public static final String INSERT_APPOINTMENT = "INSERT INTO appointment VALUES(NULL, ?, ?, ?, ?," +
            " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final User loggedInUser = new User();

    public static final DBConnector dbConnector = new DBConnector();

    private DBConnector(){}

    public void connectToDatabase() {
        try {
            dbConn = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
            dbConn.setAutoCommit(true);
        }catch(SQLException e){
            System.out.println(e);
        }
    }

    /**
     * @param username
     * @param password
     * @return Returns an empty optional if there is no matching username or password or returns a user if the
     * information matches
     * @throws SQLException
     */
    public static void login(String username, String password) throws SQLException{
        PreparedStatement userStatement = dbConn.prepareStatement(SELECT_USER_AND_PASSWORD);
        userStatement.setString(1, username);
        userStatement.setString(2, password);
        ResultSet rs = userStatement.executeQuery();
        if(rs.next()) {
            if(rs.getString(2).equalsIgnoreCase(username) && rs.getString(3).equals(password)) {
                loggedInUser.setUserID(rs.getInt(1));
                loggedInUser.setUsername(rs.getString(2));
                loggedInUser.setPassword(rs.getString(3));
            }
        }
    }

    /**
     * Loads customer data into the customer list.
     * @throws SQLException
     */
    public void setCustomers() throws SQLException {
        PreparedStatement apptStatement = dbConn.prepareStatement(SELECT_CUSTOMER_INFORMATION);

        ResultSet rsDate = apptStatement.executeQuery();
        while(rsDate.next()){
            Customer customer = new Customer();
            customer.setCustID(rsDate.getInt(1));
            customer.setCustName(rsDate.getString(2));
            customer.setAddressId(rsDate.getInt(3));
            customer.setAddress1(rsDate.getString(4));
            customer.setAddress2(rsDate.getString(5));
            customer.setPostalCode(rsDate.getString(6));
            customer.setPhoneNum(rsDate.getString(7));
            customer.setCityName(rsDate.getString(9));
            customer.setCityID(rsDate.getInt(8));
            customer.setCountryName(rsDate.getString(11));
            customer.setCountryID(rsDate.getInt(10));
            dbData.getCustomerList().add(customer);
        }
    }

    /**
     * Loads address data into the address list.
     * @throws SQLException
     */
    public void setAddresses() throws SQLException {
        PreparedStatement addressStatement = dbConn.prepareStatement(SELECT_ALL_ADDRESSES);

        ResultSet rsDate = addressStatement.executeQuery();
        while(rsDate.next()){
            Address address = new Address();
            address.setAddressID(rsDate.getInt(1));
            address.setAddress1(rsDate.getString(2));
            address.setAddress2(rsDate.getString(3));
            address.setCityID(rsDate.getInt(4));
            address.setPostalCode(rsDate.getString(5));
            address.setPhoneNum(rsDate.getString(6));
            dbData.getAddressList().add(address);
        }
    }

    /**
     * Loads the city data into the city list.
     * @throws SQLException
     */
    public void setCities() throws SQLException {
        PreparedStatement cityStatement = dbConn.prepareStatement(SELECT_ALL_CITIES);

        ResultSet rsDate = cityStatement.executeQuery();
        while(rsDate.next()){
            City city = new City();
            city.setCityID(rsDate.getInt(1));
            city.setCityName(rsDate.getString(2));
            city.setCountryID(rsDate.getInt(3));
            dbData.getCityList().add(city);
        }
    }

    /**
     * Loads the user data into the user list.
     * @throws SQLException
     */
    public void setUsers() throws SQLException {
        PreparedStatement userStatement = dbConn.prepareStatement(SELECT_ALL_USERS);

        ResultSet rsDate = userStatement.executeQuery();
        while(rsDate.next()){
            User user = new User();
            user.setUserID(rsDate.getInt(1));
            user.setUsername(rsDate.getString(2));
            user.setPassword(rsDate.getString(3));
            dbData.getUserList().add(user);
        }
    }

    /**
     * Loads the country data into the country list.
     * @throws SQLException
     */
    public void setCountries() throws SQLException {
        PreparedStatement cityStatement = dbConn.prepareStatement(SELECT_ALL_COUNTRIES);

        ResultSet rsDate = cityStatement.executeQuery();
        while(rsDate.next()){
            Country country = new Country();
            country.setCountryID(rsDate.getInt(1));
            country.setCountryName(rsDate.getString(2));
            dbData.getCountryList().add(country);
        }
    }

    /**
     * Loads the appointment data into the current user's appointment list and the appointment list of all users.
     * @throws SQLException
     */
    public void setAppointments() throws SQLException {
        PreparedStatement apptStatement = dbConn.prepareStatement(SELECT_ALL_APPOINTMENTS);
        dbData.getAppointmentList().clear();
        dbData.getReportsAppointmentList().clear();

        ResultSet rsDate = apptStatement.executeQuery();
        while(rsDate.next()){
            Appointment appointment = new Appointment();
            appointment.setAppointmentID(rsDate.getInt(1));
            appointment.setCustID(rsDate.getInt(2));
            appointment.setUserID(rsDate.getInt(3));
            appointment.setType(rsDate.getString(8));
            appointment.setStartDate(rsDate.getTimestamp(10));
            appointment.setEndDate(rsDate.getTimestamp(11));
            if(!rsDate.getTimestamp(10).before(Timestamp.valueOf(LocalDateTime.now()))
                    && appointment.getUserID() == loggedInUser.getUserID()) {
                dbData.getAppointmentList().add(appointment);//Adds current user's appointments to appointments list.
            }else if(rsDate.getTimestamp(10).before(Timestamp.valueOf(LocalDateTime.now()))){
                deleteAppointment(appointment);//Deletes old appointments.
            }else if(!rsDate.getTimestamp(10).before(Timestamp.valueOf(LocalDateTime.now()))
                    && appointment.getUserID() != loggedInUser.getUserID()) {
                dbData.getReportsAppointmentList().add(appointment);//Adds appointments of other users to reports list.
            }
        }

    }

    //
    public void insertAppointment(Appointment appointment) throws SQLException {
        PreparedStatement apptStatement = dbConn.prepareStatement(INSERT_APPOINTMENT);

        apptStatement.setInt(1, appointment.getCustID());
        apptStatement.setInt(2, loggedInUser.getUserID());
        apptStatement.setString(3, "not needed");
        apptStatement.setString(4, "not needed");
        apptStatement.setString(5, "not needed");
        apptStatement.setString(6, "not needed");
        apptStatement.setString(7, appointment.getType());
        apptStatement.setString(8, "not needed");
        apptStatement.setTimestamp(9, appointment.getStartDate());
        apptStatement.setTimestamp(10, appointment.getEndDate());
        apptStatement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
        apptStatement.setString(12, loggedInUser.getUsername());
        apptStatement.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
        apptStatement.setString(14, loggedInUser.getUsername());
        apptStatement.executeUpdate();
    }

    public void deleteCustomer(Customer customer) throws SQLException {
        dbConn.setAutoCommit(false);

        //Removes appointments associated with the customer from reports list.
        ArrayList<Appointment> appointmentsToRemove = new ArrayList<>();
        for(Appointment appointments : dbData.getReportsAppointmentList()){
            if (appointments.getCustID() == customer.getCustID()){
                dbConnector.deleteAppointment(appointments);
                appointmentsToRemove.add(appointments);
            }
        }
        dbData.getReportsAppointmentList().removeAll(appointmentsToRemove);

        appointmentsToRemove.clear(); //Prepares list for the current user's appointments.
        //Removes appointments associated with the customer from current user's appointment list.
        for(Appointment appointments : dbData.getAppointmentList()){
            if (appointments.getCustID() == customer.getCustID()){
                dbConnector.deleteAppointment(appointments);
                appointmentsToRemove.add(appointments);
            }
        }
        dbData.getAppointmentList().removeAll(appointmentsToRemove);

        PreparedStatement custStatement = dbConn.prepareStatement(DELETE_CUSTOMER);
        custStatement.setInt(1, customer.getCustID());
        custStatement.executeUpdate();

        PreparedStatement addressStatement = dbConn.prepareStatement(DELETE_ADDRESS);
        addressStatement.setInt(1, customer.getAddressId());
        addressStatement.executeUpdate();

        dbConn.commit();
        dbConn.setAutoCommit(true);
    }

    public void deleteAppointment(Appointment appointment) throws SQLException {
        PreparedStatement apptStatement = dbConn.prepareStatement(DELETE_APPOINTMENT);
        apptStatement.setInt(1, appointment.getAppointmentID());
        apptStatement.executeUpdate();
    }


    public void insertUpdateCustomer(Customer customer) throws SQLException {
        PreparedStatement custStatement = dbConn.prepareStatement(INSERT_UPDATE_CUSTOMER);

        custStatement.setInt(1, customer.getCustID());
        custStatement.setString(2, customer.getCustName());
        custStatement.setInt(3, customer.getAddressId());
        custStatement.setInt(4, 1);
        custStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        custStatement.setString(6, loggedInUser.getUsername());
        custStatement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
        custStatement.setString(8, loggedInUser.getUsername());

        custStatement.setInt(9, customer.getCustID());
        custStatement.setString(10, customer.getCustName());
        custStatement.setInt(11, customer.getAddressId());
        custStatement.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
        custStatement.setString(13, loggedInUser.getUsername());
        custStatement.executeUpdate();
    }

    public void insertUpdateCountry(Country country) throws SQLException {
        PreparedStatement countryStatement = dbConn.prepareStatement(INSERT_UPDATE_COUNTRY);

        countryStatement.setInt(1, country.getCountryID());
        countryStatement.setString(2, country.getCountryName());
        countryStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        countryStatement.setString(4, loggedInUser.getUsername());
        countryStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        countryStatement.setString(6, loggedInUser.getUsername());


        countryStatement.setInt(7, country.getCountryID());
        countryStatement.setString(8, country.getCountryName());
        countryStatement.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
        countryStatement.setString(10, loggedInUser.getUsername());
        countryStatement.executeUpdate();
    }

    public void insertUpdateCity(City city) throws SQLException {
        PreparedStatement cityStatement = dbConn.prepareStatement(INSERT_UPDATE_CITY);

        cityStatement.setInt(1, city.getCityID());
        cityStatement.setString(2, city.getCityName());
        cityStatement.setInt(3, city.getCountryID());
        cityStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        cityStatement.setString(5, loggedInUser.getUsername());
        cityStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
        cityStatement.setString(7, loggedInUser.getUsername());


        cityStatement.setInt(8, city.getCityID());
        cityStatement.setString(9, city.getCityName());
        cityStatement.setInt(10, city.getCountryID());
        cityStatement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
        cityStatement.setString(12, loggedInUser.getUsername());
        cityStatement.executeUpdate();
    }



    public void insertUpdateAddress(Address address) throws SQLException {
        PreparedStatement custStatement = dbConn.prepareStatement(INSERT_UPDATE_ADDRESS);

        custStatement.setInt(1, address.getAddressID());
        custStatement.setString(2, address.getAddress1());
        custStatement.setString(3, address.getAddress2());
        custStatement.setInt(4, address.getCityID());
        custStatement.setString(5, address.getPostalCode());
        custStatement.setString(6, address.getPhoneNum());
        custStatement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
        custStatement.setString(8, loggedInUser.getUsername());
        custStatement.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
        custStatement.setString(10, loggedInUser.getUsername());

        custStatement.setInt(11, address.getAddressID());
        custStatement.setString(12, address.getAddress1());
        custStatement.setString(13, address.getAddress2());
        custStatement.setInt(14, address.getCityID());
        custStatement.setString(15, address.getPostalCode());
        custStatement.setString(16, address.getPhoneNum());
        custStatement.setTimestamp(17, Timestamp.valueOf(LocalDateTime.now()));
        custStatement.setString(18, loggedInUser.getUsername());
        custStatement.executeUpdate();
    }


    public void updateAppointment(Appointment appointment) throws SQLException {
        PreparedStatement apptStatement = dbConn.prepareStatement(UPDATE_APPOINTMENT);

        apptStatement.setInt(1, appointment.getCustID());
        apptStatement.setString(2, appointment.getType());
        apptStatement.setTimestamp(3, appointment.getStartDate());
        apptStatement.setTimestamp(4, appointment.getEndDate());
        apptStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
        apptStatement.setString(6, loggedInUser.getUsername());
        apptStatement.setInt(7, appointment.getAppointmentID());
        apptStatement.executeUpdate();
    }



    public void closeConnection(){
        try {
            dbConn.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Error: "+e);
            e.printStackTrace();
        }
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }
}
