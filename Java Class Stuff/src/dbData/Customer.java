package dbData;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import static dbData.DBData.*;

public class Customer implements Comparable{

    private int custID;
    private String custName;

    private int addressId;
    private String address1;
    private String address2;
    private String postalCode;
    private String phoneNum;

    private int cityID;
    private String cityName;

    private int countryID;
    public String countryName;

    private StringProperty custNameProp = new SimpleStringProperty();
    private StringProperty address1Prop = new SimpleStringProperty();
    private StringProperty address2Prop = new SimpleStringProperty();
    private StringProperty postalProp = new SimpleStringProperty();
    private StringProperty phoneProp = new SimpleStringProperty();
    private StringProperty cityNameProp = new SimpleStringProperty();
    private StringProperty countryNameProp = new SimpleStringProperty();


    public String getAddress1() {
        return address1;
    }

    /**Sets the address and generates a new addressID and adds the new address to addressList if it is a new address
     *
     * @param address1 first address line
     * @param address2 second addres line
     * @param postalCode
     * @param phoneNum
     */
    public void setAddress(String address1, String address2, String postalCode, String phoneNum) {
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.phoneNum = phoneNum;

        address1PropProperty().set(address1);
        address2PropProperty().set(address2);
        postalPropProperty().set(postalCode);
        phonePropProperty().set(phoneNum);

        int maxID = 1;
        for(Address addresses : dbData.getAddressList()){
            maxID = Math.max(maxID, addresses.getAddressID());
            if(addresses.matches(address1, address2, postalCode, phoneNum)) {
                addressId = addresses.getAddressID();
                return;
            }
        }
        addressId = maxID+1;
        dbData.getAddressList().add(new Address(addressId, address1, address2, cityID, postalCode, phoneNum));
    }



    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        address2PropProperty().set(address2);
        this.address2 = address2;
    }

    public void setAddress1(String address1) {
        address1PropProperty().set(address1);
        this.address1 = address1;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        postalPropProperty().set(postalCode);
        this.postalCode = postalCode;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        phonePropProperty().set(phoneNum);
        this.phoneNum = phoneNum;
    }

    public String getCityName() {
        return cityName;
    }

    /**Sets the city name and generates a new cityID and adds the name to cityList if it is a new city
     *
     * @param cityName
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
        cityNamePropProperty().set(cityName);
        int maxID = -1;
        for(City cities : dbData.getCityList()){
            maxID = Math.max(maxID, cities.getCityID());
            if(cities.getCityName().equalsIgnoreCase(cityName)) {
                cityID = cities.getCityID();
                return;
            }
        }
        cityID = maxID+1;
        dbData.getCityList().add(new City(cityID, cityName, countryID));
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    /**Sets the country name and generates a new countryID and adds the name to countryList if it is a new country
     *
     * @param countryName
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
        countryNamePropProperty().set(countryName);
        int maxID = -1;
        for(Country country : dbData.getCountryList()){
            maxID = Math.max(maxID, country.getCountryID());
            if(country.getCountryName().equalsIgnoreCase(countryName)) {
                countryID = country.getCountryID();
                return;
            }
        }
        countryID = maxID+1;
        dbData.getCountryList().add(new Country(countryName, maxID+1));
    }

    public int getCustID() {
        return custID;
    }

    public void setCustID(int custID) {
        this.custID = custID;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String name) {
        custNamePropProperty().set(name);
        this.custName = name;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.compare(this.custID, ((Customer) o).custID);
    }

    public String toString(){
        return custName;
    }


    public String getCustNameProp() {
        return custNameProp.get();
    }

    public StringProperty custNamePropProperty() {
        return custNameProp;
    }

    public void setCustNameProp(String custNameProp) {
        this.custNameProp.set(custNameProp);
    }

    public String getAddress1Prop() {
        return address1Prop.get();
    }

    public StringProperty address1PropProperty() {
        return address1Prop;
    }

    public void setAddress1Prop(String address1Prop) {
        this.address1Prop.set(address1Prop);
    }

    public String getAddress2Prop() {
        return address2Prop.get();
    }

    public StringProperty address2PropProperty() {
        return address2Prop;
    }

    public void setAddress2Prop(String address2Prop) {
        this.address2Prop.set(address2Prop);
    }

    public String getPostalProp() {
        return postalProp.get();
    }

    public StringProperty postalPropProperty() {
        return postalProp;
    }

    public void setPostalProp(String postalProp) {
        this.postalProp.set(postalProp);
    }

    public String getPhoneProp() {
        return phoneProp.get();
    }

    public StringProperty phonePropProperty() {
        return phoneProp;
    }

    public void setPhoneProp(String phoneProp) {
        this.phoneProp.set(phoneProp);
    }

    public String getCityNameProp() {
        return cityNameProp.get();
    }

    public StringProperty cityNamePropProperty() {
        return cityNameProp;
    }

    public void setCityNameProp(String cityNameProp) {
        this.cityNameProp.set(cityNameProp);
    }

    public String getCountryNameProp() {
        return countryNameProp.get();
    }

    public StringProperty countryNamePropProperty() {
        return countryNameProp;
    }

    public void setCountryNameProp(String countryNameProp) {
        this.countryNameProp.set(countryNameProp);
    }
}
