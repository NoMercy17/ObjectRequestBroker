import RequestReply.*;
import ByteSendReceive.*;
import Commons.Address;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// we use InfoClientProxy to access the InfoService
// InfoClient - InfoClientProxy - NamingService - InfoServerProxy - InfoServer
public class InfoClient {
    private final InfoClientProxy serviceProxy;

    public InfoClient() {
        this.serviceProxy = new InfoClientProxy();
    }

    public static void main(String[] args){
        InfoClient client = new InfoClient();
        client.run();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        try {
            boolean running = true;

            System.out.println("InfoClient started");
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
                    String serverList = serviceProxy.listAvailableServers();
                    System.out.println("Registered servers:\n" + serverList);
                    continue;
                } else if (command.equals("refresh")) {
                    serviceProxy.refresh();
                    System.out.println("Successfully reconnected to the server");
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
                            result = serviceProxy.getRoadInfo(roadID);
                        } catch (NumberFormatException e) {
                            result = "Invalid road ID format. Please enter a number.";
                        }
                        break;
                    case "temp":
                        result = serviceProxy.getTemp(parameter);
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