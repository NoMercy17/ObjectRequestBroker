import RequestReply.*;
import ByteSendReceive.*;
import Commons.Address;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InfoClient {
    private final Requestor requestor;
    private Address serverAddress;
    private final String clientName;
    private final ServiceLocator serviceLocator;

    public InfoClient(String clientName) {
        this.clientName = clientName;
        this.requestor = new Requestor(clientName);
        this.serviceLocator = new ServiceLocator();

        // Look up the server address at initialization
        lookupServerAddress();
    }

    public void lookupServerAddress(){
        Address address = serviceLocator.lookupServer("InfoServer");
        if (address != null) {
            this.serverAddress = address;
            System.out.println("Found InfoServer at " + address.getHost() + ":" + address.getPort());
        } else {
            System.err.println("Could not find InfoServer. Make sure the server is running and registered.");
        }
    }

    public String getRoadInfo(int roadID){
        if (serverAddress == null) {
            return "Error: Server address is not available";
        }

        try{
            // request format
            String request = "get_road_info:" + roadID;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8); //

            // send request
            byte[] responseBytes = requestor.deliver_and_wait(serverAddress, requestBytes);

            // convert and return response
            return new String(responseBytes, StandardCharsets.UTF_8);

        }catch(Exception e){
            return "Error1: " +e.getMessage();
        }
    }

    public String getTemp(String city) {
        if (serverAddress == null) {
            return "Error: Server address is not available";
        }

        try {
            // Format the request
            String request = "get_temp:" + city;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);

            // Send the request and get the response
            byte[] responseBytes = requestor.deliver_and_wait(serverAddress, requestBytes);

            // Convert and return the response
            return new String(responseBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Error2: " + e.getMessage();
        }
    }
    public String listAvailableServers() {
        return serviceLocator.listServers();
    }

    public static void main(String[] args) {
        String clientName = "InfoClient";
        InfoClient client = new InfoClient(clientName);
        Scanner scanner = new Scanner(System.in);

        try {
            boolean running = true;

            System.out.println(clientName + " started");
            System.out.println("Available commands:");
            System.out.println("1. road <road_id> - Get road information");
            System.out.println("2. temp <city> - Get temperature for a city");
            System.out.println("3. servers - List all registered servers");
            System.out.println("4. refresh - Lookup the InfoServer again");
            System.out.println("5. exit - Exit the client");

            while (running) {
                System.out.print("> ");
                String command = scanner.nextLine().trim();

                if (command.equals("exit")) {
                    running = false;
                    continue;
                } else if (command.equals("servers")) {
                    String serverList = client.listAvailableServers();
                    System.out.println("Registered servers:\n" + serverList);
                    continue;
                } else if (command.equals("refresh")) {
                    client.lookupServerAddress();
                    continue;
                }

                String[] parts = command.split("\\s+", 2);
                if (parts.length < 2) {
                    System.out.println("Invalid command");
                    continue;
                }

                String operation = parts[0];
                String parameter = parts[1];
                String result;

                switch (operation) {
                    case "road":
                        try {
                            int roadID = Integer.parseInt(parameter);
                            result = client.getRoadInfo(roadID);
                        } catch (NumberFormatException e) {
                            result = "Invalid road ID format. Please enter a number.";
                        }
                        break;
                    case "temp":
                        result = client.getTemp(parameter);
                        break;
                    default:
                        result = "Unknown command";
                }

                System.out.println(result);
            }
        } finally {
            scanner.close();
        }
    }
}