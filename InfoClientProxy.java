import RequestReply.*;
import Commons.Address;
import java.nio.charset.StandardCharsets;

public class InfoClientProxy {
    private final Requestor requestor;
    private Address serverAddress;
    private final ServiceLocator serviceLocator;
    private final String Server_name = "InfoServer";

    public InfoClientProxy(){
        this.requestor = new Requestor("InfoClientProxy");
        this.serviceLocator = new ServiceLocator();

        lookupServerAddress();
    }

    private void lookupServerAddress(){
        Address address  = serviceLocator.lookupServer(Server_name);
        if (address != null) {
            this.serverAddress = address;
            System.out.println("Found " + Server_name + " at " + address.getHost() + ":" + address.getPort());
        } else {
            System.err.println("Could not find " + Server_name + ". Make sure the server is running and registered.");
        }
    }

    // to research again for the server address
    public boolean refresh() {
        lookupServerAddress();
        return (serverAddress != null);
    }

    public String getRoadInfo(int roadID){
        if(serverAddress == null){
            return "Error: Server address is not available";
        }

        try{
            String request = "get_road_info:" + roadID;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);

            // Send request and get response
            byte[] responseBytes = requestor.deliver_and_wait(serverAddress, requestBytes);

            // Convert and return response
            return new String(responseBytes, StandardCharsets.UTF_8);
        }catch(Exception e){
            return "Error: " + e.getMessage();
        }
    }

    public String getTemp(String city){
        try{
            String request = "get_temp:" + city;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);

            // Send request and get response
            byte[] responseBytes = requestor.deliver_and_wait(serverAddress, requestBytes);

            // Convert and return response
            return new String(responseBytes, StandardCharsets.UTF_8);

        }catch(Exception e){
            return "Error: " + e.getMessage();
        }
    }

    public String listAvailableServers() {
        return serviceLocator.listServers();
    }
}
