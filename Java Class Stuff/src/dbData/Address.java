package dbData;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class Address implements Comparable<Address>{

    private int addressID;
    private String address1;
    private String address2;
    private int cityID;
    private String postalCode;
    private String phoneNum;


    public Address(int addressID, String address1, String address2, int cityID, String postalCode, String phoneNum) {
        this.addressID = addressID;
        this.address1 = address1;
        this.address2 = address2;
        this.cityID = cityID;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;
    }

    public Address() {}

    public int getAddressID(){
        return addressID;
    }

    public void setAddressID(int addressID) {
        this.addressID = addressID;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    /**
     * Checks to see if the provided address matches the data in the current address.
     * @param address
     * @return Returns true if the data matches or false if it does not.
     */
    @Override
    public int compareTo(Address address) {
        if((address.address1.equalsIgnoreCase(address1))
                && (address.address2.equalsIgnoreCase(address2))
                && (address.postalCode.equalsIgnoreCase(postalCode))
                && (address.phoneNum.equalsIgnoreCase(phoneNum)) )
            return 0;
        else
            return 1;
    }

    /**
     * Checks to see if the parameters match the data in the address.
     * @param address1
     * @param address2
     * @param postalCode
     * @param phoneNum
     * @return Returns true if the data matches or false if it does not.
     */
    public boolean matches(String address1, String address2, String postalCode, String phoneNum) {
        if((this.address1.equalsIgnoreCase(address1))
                && (this.address2.equalsIgnoreCase(address2))
                && (this.postalCode.equalsIgnoreCase(postalCode))
                && (this.phoneNum.equalsIgnoreCase(phoneNum)) )
        return true;
        else
            return false;
    }
}
