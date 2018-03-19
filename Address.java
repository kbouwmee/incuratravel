public class Address {

    private String street;
    private String postalcode;
    private String city;

    public Address(String s, String p, String c) {
        this.street = s;
        this.postalcode = p;
        this.city = c;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public String getCity() {
        return city;
    }

    public String toString() {
        return street + " " + postalcode + " " + city;
    }
}