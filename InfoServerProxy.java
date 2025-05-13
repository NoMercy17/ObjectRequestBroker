import RequestReply.*;
import Commons.Address;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import Service.*;

public class InfoServerProxy {
    private final Replyer replyer;
    private final InfoService service;
    private final Address serverAddress;
    private final String Server_name = "InfoServer";
    private final int PORT = 8090;

    public InfoServerProxy() throws IOException {
        this.service = new InfoServiceImplementation();
        this.serverAddress = new Address("localhost", PORT);
        this.replyer = new Replyer(Server_name, serverAddress);

        registerWithNamingService();
    }

    private void registerWithNamingService(){
        try{
            ServiceLocator locator = new ServiceLocator();
            boolean registered = locator.registerServer(Server_name, serverAddress.getHost(), serverAddress.getPort());
            if (registered) {
                System.out.println("Successfully registered with NamingService");
            } else {
                System.err.println("Failed to register with NamingService");
            }
        }catch(Exception e){
            System.err.println("Error registering with NamingService: " + e.getMessage());
        }
    }

    public void start(){
        System.out.println(Server_name  + " started on port " + PORT);
        System.out.println("Waiting for requests");

        ByteStreamTransformer transformer = new RequestTransformer();
        try{
            while (true) {
                replyer.receive_transform_send(transformer);
            }
        }catch(Exception e){
            System.err.println("Error in server proxy main loop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class RequestTransformer implements ByteStreamTransformer {
        @Override
        public byte[] transform(byte[] request) {
            // Convert request bytes to string for processing
            String requestStr = new String(request, StandardCharsets.UTF_8);
            System.out.println("Received request: " + requestStr);

            // Parse the operation and parameters
            String[] parts = requestStr.split(":");
            String operation = parts[0];
            String result;

            try {
                // Dispatch to the appropriate method on the service implementation
                switch (operation) {
                    case "get_road_info":
                        int roadId = Integer.parseInt(parts[1]);
                        result = service.getRoadInfo(roadId);
                        break;
                    case "get_temp":
                        String city = parts[1];
                        result = service.getTemp(city);
                        break;
                    default:
                        result = "Unknown operation: " + operation;
                }
            } catch (NumberFormatException e) {
                result = "Invalid parameter format: " + e.getMessage();
            } catch (Exception e) {
                result = "Error processing request: " + e.getMessage();
                e.printStackTrace();
            }

            System.out.println("Sending response: " + result);
            return result.getBytes(StandardCharsets.UTF_8);
        }
    }

    public static void main(String[] args) {
        try {
            InfoServerProxy serverProxy = new InfoServerProxy();
            serverProxy.start();
        } catch (IOException e) {
            System.err.println("Failed to start server proxy: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
