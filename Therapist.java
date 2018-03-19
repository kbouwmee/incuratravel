public class Therapist {

    private String name;
    private Address address;
    private String csvString;

    public Therapist(String name, Address address, String c) {
        this.name = name;
        this.address = address;
        this.csvString = c;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public String getCsvString() {
        return csvString;
    }
}