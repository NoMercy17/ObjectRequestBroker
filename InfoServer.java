import RequestReply.*;
import Commons.Address;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class InfoServer {
    private final Replyer replyer;
    private static final int PORT = 8090; // Changed from 8080 to 8090
    private static final String SERVER_NAME = "InfoServer";
    private final Address serverAddress;
    public InfoServer() throws IOException {
        try {
            this.serverAddress = new Address("localhost", PORT);
            this.replyer = new Replyer(SERVER_NAME, serverAddress);
            System.out.println(SERVER_NAME + " started on port " + PORT);

            // we register with NamingService now
            registerWithNamingService();
        } catch (IOException e) {
            System.err.println("Failed to create server socket: " + e.getMessage());
            throw e; // Re-throw to signal failure
        }
    }

    private void registerWithNamingService(){
        try{
            ServiceLocator locator = new ServiceLocator();
            boolean registered = locator.registerServer(SERVER_NAME, serverAddress.getHost(), serverAddress.getPort());
            if (registered) {
                System.out.println("Successfully registered with NamingService");
            }else
                System.out.println("Failed to register with NamingService");

        }catch(Exception e){
            System.err.println("Error registering with NamingService: " + e.getMessage());
        }
    }


    public void start() {
        System.out.println("Waiting for requests...");
        ByteStreamTransformer transformer = new InfoServerTransformer();

        try {
            while (true) {
                // Process incoming requests
                replyer.receive_transform_send(transformer);
            }
        } catch (Exception e) {
            System.err.println("Error in server main loop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Inner class with ByteStreamTransformer interface
    //to process incoming requests and generate responses
    private class InfoServerTransformer implements ByteStreamTransformer {
        @Override
        public byte[] transform(byte[] request) {
            // Convert request bytes to string for processing
            String requestStr = new String(request, StandardCharsets.UTF_8);
            System.out.println("Received request: " + requestStr);

            // Parse the operation and param
            String[] parts = requestStr.split(":");
            if (parts.length < 2) {
                return "Invalid request format".getBytes(StandardCharsets.UTF_8);
            }

            String operation = parts[0];
            String result;

            switch (operation) {
                case "get_road_info":
                    try {
                        int roadId = Integer.parseInt(parts[1]);
                        result = getRoadInfo(roadId);
                    } catch (NumberFormatException e) {
                        result = "Invalid road ID format";
                    }
                    break;
                case "get_temp":
                    String city = parts[1];
                    result = getTemp(city);
                    break;
                default:
                    result = "Unknown operation";
            }

            System.out.println("Sending response: " + result);
            return result.getBytes(StandardCharsets.UTF_8);
        }
    }

    // Implementation of service methods
    private String getRoadInfo(int roadId) {
        // Sample implementation
        switch (roadId) {
            case 1:
                return "Highway A1: Normal traffic conditions";
            case 2:
                return "Highway A2: Heavy traffic due to construction";
            case 3:
                return "Route 66: Clear road";
            default:
                return "No information available for road ID " + roadId;
        }
    }

    private String getTemp(String city) {
        switch (city.toLowerCase()) {
            case "amsterdam":
                return "Amsterdam: 15°C";
            case "berlin":
                return "Berlin: 18°C";
            case "paris":
                return "Paris: 22°C";
            case "rome":
                return "Rome: 25°C";
            default:
                return "No temperature data available for " + city;
        }
    }

    public static void main(String[] args) {
        try {
            InfoServer server = new InfoServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}