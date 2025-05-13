package Service;

// Implementation of the InfoService interface
// Service implementation that will be invoked by the server-side proxy

public class InfoServiceImplementation implements InfoService{

    public String getRoadInfo(int roadID){
        switch (roadID) {
            case 1:
                return "Highway A1: Normal traffic conditions";
            case 2:
                return "Highway A2: Heavy traffic due to construction";
            case 3:
                return "Route 66: Clear road";
            default:
                return "No information available for road ID " + roadID;
        }
    }

    public String getTemp(String city){
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
}
