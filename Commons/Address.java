package Commons;

public class Address {
    private final String destination;
    private final int portNumber;

    public Address(String dest, int port) {
        this.destination = dest;
        this.portNumber = port;
    }
    public String dest(){
        return destination;
    }
    public int port(){
        return portNumber;
    }
}
