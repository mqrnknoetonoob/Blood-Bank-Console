package Client;

import StuffRelated.*;
import java.io.*;
import java.io.ObjectOutputStream;

public class ClientMenuHandler {
    public static void handleMenu(String role, Stuff currentStuff, ObjectOutputStream out, ObjectInputStream in) throws IOException {
        switch (role) {
            case "Manager" -> ManagerClient.run(currentStuff, out);
            case "Registration Tester" -> RegTesterClient.run(currentStuff, out);
            case "Donation Officer" -> DonationOfficerClient.run(currentStuff, out);
            case "Preservation Officer" -> PreservationOfficerClient.run(currentStuff, out);
            case "Delivery Tester" -> DeliveryTesterClient.run(currentStuff, out);
            default -> System.out.println("Invalid role. Cannot start client menu.");
        }
    }
}