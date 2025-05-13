import RequestReply.*;
import Commons.Address;
import java.nio.charset.StandardCharsets;

public class ServiceLocator {
    private final Requestor requestor;
    private final Address namingServiceAddress;

    // Default for NamingService
    private static final String NAMING_SERVICE_HOST = "localhost";
    private static final int NAMING_SERVICE_PORT = 8085;

    public ServiceLocator(){
        this(NAMING_SERVICE_HOST, NAMING_SERVICE_PORT);
    }
    public ServiceLocator(String host, int port){
        this.requestor = new Requestor(host);
        this.namingServiceAddress = new Address(host, port);
    }

    public boolean registerServer(String serverName, String host, int port){
        try{
            String request = "register:" + serverName + ":" + host + ":" + port;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);

            byte[] responseBytes = requestor.deliver_and_wait(namingServiceAddress, requestBytes);
            String response = new String(responseBytes, StandardCharsets.UTF_8);

            return "OK".equals(response);
        }catch(Exception e){
            System.err.println("Error registering server: " + e.getMessage());
            return false;
        }
    }

    public Address lookupServer(String serverName){
        try{
            String request = "lookup:" + serverName;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);

            byte[] responseBytes = requestor.deliver_and_wait(namingServiceAddress, requestBytes);
            String response = new String(responseBytes, StandardCharsets.UTF_8);

            if("NOT_FOUND".equals(response)){
                System.out.println("Server not found: " + serverName);
                return null;
            }

            String[] parts = response.split(":");
            if(parts.length != 2){
                System.err.println("Server lookup response: " + response);
                return null;
            }

            String host = parts[0];
            int port = Integer.parseInt(parts[1]);

            return new Address(host, port);

        }catch(Exception e){
            System.err.println("Error looking up server: " + e.getMessage());
            return null;
        }
    }

    public String listServers(){
        try{
            String request = "list";
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);

            byte[] responseBytes = requestor.deliver_and_wait(namingServiceAddress, requestBytes);
            return new String(responseBytes, StandardCharsets.UTF_8);
        }catch(Exception e){
            System.err.println("Error listing servers: " + e.getMessage());
            return "Errorrr with the list!";
        }

    }


}
