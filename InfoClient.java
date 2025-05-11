import RequestReply.*;
import ByteSendReceive.*;
import Commons.Address;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class InfoClient {
    private final Requestor requestor;
    private final Address serverAddress;
    private final String clientName;

    public InfoClient(String clientName, String serverHost, int serverPort) {
        this.clientName = clientName;
        this.serverAddress = new Address(serverHost, serverPort);
        this.requestor = new Requestor(clientName);
    }

    public String getRoadInfo(int roadID){
        try{
            // request format
            String request = "get_road_info:" + roadID;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8); //

            // send request
            byte[] responseBytes = requestor.deliver_and_wait(serverAddress, requestBytes);

            // convert and return response
            return new String(responseBytes, StandardCharsets.UTF_8);

        }catch(Exception e){
            return "Errror: " +e.getMessage();
        }
    }

    public String getTemp(String city) {
        try {
            // Format the request
            String request = "get_temp:" + city;
            byte[] requestBytes = request.getBytes(StandardCharsets.UTF_8);

            // Send the request and get the response
            byte[] responseBytes = requestor.deliver_and_wait(serverAddress, requestBytes);

            // Convert and return the response
            return new String(responseBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 8080;
        String clientName = "InfoClient";

        if(args.length >= 1){
            serverHost = args[0];
        }
        if(args.length >= 2){
            try{
                serverPort = Integer.parseInt(args[1]);
            }catch(NumberFormatException e){
                System.out.println("Invalid port number");
            }
        }
        InfoClient client = new InfoClient(clientName, serverHost, serverPort);
        Scanner scanner = new Scanner(System.in);

        try{
            boolean running = true;

            System.out.println(clientName + " connected to " + serverHost + ":" + serverPort);
            System.out.println("Available commands:");
            System.out.println("1. road <road_id> - Get road information");
            System.out.println("2. temp <city> - Get temperature for a city");
            System.out.println("3. exit - Exit the client");

            while(running){
                System.out.println("> ");
                String command = scanner.nextLine().trim();

                if(command.equals("exit")){
                    running = false;
                }
                String[] parts = command.split("\\s+", 2); // split the string by whitespace, no more than 2 elements
                if(parts.length < 2){
                    System.out.println("Invalid command");
                }

                String operation = parts[0];
                String parameter = parts[1];
                String result;
                switch(operation){
                    case "road":
                        try{
                            int roadID = Integer.parseInt(parameter);
                            result = client.getRoadInfo(roadID);
                        }catch(NumberFormatException e){
                            result = "Invalid road ID format. Please enter a number.";
                        }
                    case "temp":
                        result = client.getTemp(parameter);
                        break;
                    default:
                        result = "Unknwon command";

                }
                System.out.println(result);
            }
        }finally {
            scanner.close();
        }

    }
}
