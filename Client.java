public class Client {

    private String name;
    private int id;
    private Address address;

    public Client(String name, int id, Address address) {
        this.name = name;
        this.id = id;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public Address getAddress() {
        return this.address;
    }

    public String toJSON() {
        return "{ 'id': "+id + "," +
                "'name': '"+name + "'," +
                "'street': '"+address.getStreet() + "'," +
                "'postalcode': '"+address.getPostalcode() + "'," +
                "'city': '"+address.getCity() + "'}"; 
    }
}