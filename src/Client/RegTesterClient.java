package Client;

import KitRelated.KitManagement;
import StuffRelated.*;
import BloodRelated.*;
import Message.*;

import java.io.IOException;
import java.util.Scanner;
import java.io.ObjectOutputStream;

public class RegTesterClient {
    public static void run(Stuff currentStuff, ObjectOutputStream out) {
        Scanner sc = new Scanner(System.in);
        RegTester regTester = (RegTester) currentStuff;
        //KitManagement z = new KitManagement(10,10,10,10);

        while (true) {
            System.out.println("\nRegistration Tester Menu:");
            System.out.println("1. View Profile");
            System.out.println("2. Register Donor and Check Health");
            System.out.println("0. Logout");

            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    regTester.showInfo();
                    break;
                case 2:
                    System.out.println("Enter Donor Information:");
                    System.out.print("Name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("NID: ");
                    String nid = sc.nextLine().trim();
                    System.out.print("Blood Group (e.g., A+, O-): ");
                    String bloodGroup = sc.nextLine().trim().toUpperCase();
                    System.out.println("How much testing Kit used:");
                    int p = sc.nextInt();
                    sc.nextLine();
                    KitManagement z = new KitManagement();
                    regTester.kitInfoUpdater(p, z);
                    z.saveToFile();
                    System.out.print("Health Check Passed (true/false): ");
                    boolean healthPassed;
                    try {
                        healthPassed = Boolean.parseBoolean(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Invalid input. Please enter true or false.");
                        continue;
                    }

                    Person donor = regTester.registerDonor(name, nid, bloodGroup);
                    AddingRequestMessage msg = regTester.checkHealth(healthPassed, donor);
                    if (msg != null) {
                        try {
                            out.writeObject(msg);
                            out.flush();
                            System.out.println("Donor health check passed. Message sent to Donation Officer.");
                        } catch (IOException e) {
                            System.out.println("Error sending message: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Donor health check failed. No message sent.");
                    }
                    break;
                case 0:
                    try {
                        out.writeObject("LOGOUT");
                        out.flush();
                        System.out.println("Logging out...");
                        return;
                    } catch (IOException e) {
                        System.out.println("Error during logout: " + e.getMessage());
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}