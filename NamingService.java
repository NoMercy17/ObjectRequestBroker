import RequestReply.*;
import Commons.Address;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NamingService {
    private final Replyer replyer;
    private static final int PORT = 8085;
    private static final String SERVER_NAME = "NamingService";

    private final Map<String, Address> registeredServers = new HashMap<>();

    public NamingService() throws IOException{
        try{
            Address serverAddr = new Address("localhost", PORT);
            this.replyer = new Replyer(SERVER_NAME, serverAddr);
            System.out.println(SERVER_NAME + " started on port " + PORT);
        }catch(IOException e){
            System.err.println("Failed to create NamingService socket: "+ e.getMessage());
            throw e;
        }
    }

    public void start(){
        System.out.println("NamingService running. Waiting for requests...");
        ByteStreamTransformer transformer = new NamingServiceTransformer();

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
    private class NamingServiceTransformer implements ByteStreamTransformer {
        @Override
        public byte[] transform(byte[] request) {
            String requestStr = new String(request, StandardCharsets.UTF_8);
            System.out.println("Received request: " + requestStr);

            String[] parts = requestStr.split(":");
            if (parts.length < 2) {
                return "Invalid request format".getBytes(StandardCharsets.UTF_8);
            }

            String operation = parts[0];
            String result;

            switch (operation) {
                case "register":
                    if (parts.length < 4) {
                        result = "Invalid registration format";
                    } else {
                        String serverName = parts[1];
                        String host = parts[2];
                        int port;
                        try {
                            port = Integer.parseInt(parts[3]);
                            registerServer(serverName, host, port);
                            result = "OK";
                        } catch (NumberFormatException e) {
                            result = "Invalid port number";
                        }
                    }
                    break;
                case "lookup":
                    if (parts.length < 2) {
                        result = "Invalid lookup format";
                    } else {
                        String serverName = parts[1];
                        result = lookupServer(serverName);
                    }
                    break;
                case "list":
                    result = listServers();
                    break;
                default:
                    result = "Unknown operation";
            }

            System.out.println("Sending response: " + result);
            return result.getBytes(StandardCharsets.UTF_8);
        }
    }

    private synchronized void registerServer(String serverName, String host, int port) {
        Address serverAddress = new Address(host,port);
        registeredServers.put(serverName, serverAddress);
        System.out.println("Registered server: " + serverName + " at " + host + ":" + port);
    }

    private synchronized String lookupServer(String serverName) {
        Address serverAddress = registeredServers.get(serverName);
        if (serverAddress == null) {
            return "DOESN'T EXIST";
        }
        return serverAddress.getHost() + ":" + serverAddress.getPort();
    }

    private synchronized String listServers() {
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, Address> entry: registeredServers.entrySet()) {
            sb.append(entry.getKey()).append(" at ")
                    .append(entry.getValue().getHost()).append(":")
                    .append(entry.getValue().getPort()).append("\n");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        try {
            NamingService namingService = new NamingService();
            namingService.start();
        } catch (IOException e) {
            System.err.println("Failed to start NamingService: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
