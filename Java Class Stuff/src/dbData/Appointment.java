package dbData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Timestamp;

import static dbData.DBData.dbData;

public class Appointment implements Comparable{

    private int appointmentID;
    private int custID;
    private int userID;
    private Timestamp startDate;
    private Timestamp endDate;
    private String type;

    private StringProperty custIDProp = new SimpleStringProperty();
    private StringProperty dateProp = new SimpleStringProperty();
    private StringProperty startDateProp = new SimpleStringProperty();
    private StringProperty endDateProp = new SimpleStringProperty();
    private StringProperty typeProp = new SimpleStringProperty();


    public String toString(){
        return "[ID]: " + appointmentID + " [User]: " + dbData.findUserByID(userID).get() + " [Cust]: "
                + dbData.findCustomerByID(custID).get() + " [Date]: " + startDate + "  [Type]: " + type;
    }


    public String getDateProp() {
        return dateProp.get();
    }

    public StringProperty datePropProperty() {
        return dateProp;
    }

    public void setDateProp(String dateProp) {
        this.dateProp.set(dateProp);
    }

    public String getCustIDProp() {
        return custIDPropProperty().get();
    }

    public StringProperty custIDPropProperty() {
        if (custIDProp == null)
            custIDProp = new SimpleStringProperty(this, "custIDProp");
        return custIDProp;
    }

    public void setCustIDProp(String custIDProp) {
        custIDPropProperty().set(custIDProp);
    }

    public String getStartDateProp() {
        return startDatePropProperty().get();
    }

    public StringProperty startDatePropProperty() {
        if (startDateProp == null) startDateProp = new SimpleStringProperty(this, "startDateProp");
        return startDateProp;
    }

    public void setStartDateProp(String startDateProp) {
        startDatePropProperty().set(startDateProp);
    }

    public String getEndDateProp() {
        return endDatePropProperty().get();
    }

    public StringProperty endDatePropProperty() {
        if (endDateProp == null) endDateProp = new SimpleStringProperty(this, "endDateProp");
        return endDateProp;
    }

    public void setEndDateProp(String endDateProp) {
        endDatePropProperty().set(endDateProp);
    }

    public String getTypeProp() {
        return typePropProperty().get();
    }

    public StringProperty typePropProperty() {
        if (typeProp == null) typeProp = new SimpleStringProperty(this, "typeProp");
        return typeProp;
    }

    public void setTypeProp(String typeProp) {
        typePropProperty().set(typeProp);
    }

    public Appointment(){
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        typePropProperty().set(type);
        this.type = type;
    }

    public int getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public int getCustID() {
        return custID;
    }

    public void setCustID(int custID) {
        custIDPropProperty().set(dbData.findCustomerByID(custID).get().getCustName());
        this.custID = custID;
    }

    public void setStartDate(Timestamp timestamp) {
        startDatePropProperty().set(timestamp.toLocalDateTime().toLocalTime().toString());
        datePropProperty().set(timestamp.toLocalDateTime().toLocalDate().toString());
        this.startDate = timestamp;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp timestamp) {
        endDatePropProperty().set(timestamp.toLocalDateTime().toLocalTime().toString());
        endDate = timestamp;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.appointmentID, ((Appointment) o).appointmentID);
    }

    public void createNewID() {
        int maxID = -1;
        for(Appointment appointments : dbData.getAppointmentList()){
            maxID = Math.max(maxID, appointments.getAppointmentID());
        }
        appointmentID = maxID+1;
    }
}
