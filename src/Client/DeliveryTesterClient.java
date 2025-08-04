/*package Client;

import StuffRelated.*;
import Message.*;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Scanner;
import BloodRelated.*;
import KitRelated.*;

public class DeliveryTesterClient {
    public static void run(Stuff currentStuff, ObjectOutputStream out) {
        Scanner sc = new Scanner(System.in);
        DeliveryTester deliveryTester = (DeliveryTester) currentStuff;

        while (true) {
            System.out.println("\nDelivery Tester Menu:");
            System.out.println("1. View Profile");
            System.out.println("2. Test Received Blood Unit");
            System.out.println("3. Send Customer Request");
            System.out.println("4. Update Kit Usage");
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
                    deliveryTester.profilePresenter();
                    break;
                case 2:
                    // Assume blood unit received via message
                    System.out.println("Enter Blood Unit Details (from received message):");
                    System.out.print("Donor Name: ");
                    String name = sc.nextLine().trim();
                    System.out.print("Donor NID: ");
                    String nid = sc.nextLine().trim();
                    System.out.print("Blood Group: ");
                    String bloodGroup = sc.nextLine().trim().toUpperCase();
                    System.out.print("Donation Date (YYYY-MM-DD): ");
                    String donationDateStr = sc.nextLine().trim();
                    System.out.print("Expiry Date (YYYY-MM-DD): ");
                    String expiryDateStr = sc.nextLine().trim();
                    System.out.print("Test Passed (true/false): ");
                    boolean testPassed;
                    try {
                        testPassed = Boolean.parseBoolean(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Invalid input. Please enter true or false.");
                        continue;
                    }

                    try {
                        java.time.LocalDate donationDate = java.time.LocalDate.parse(donationDateStr);
                        java.time.LocalDate expiryDate = java.time.LocalDate.parse(expiryDateStr);
                        BloodUnit bloodUnit = new BloodUnit(name, nid, bloodGroup, donationDate, expiryDate);
                        AddingRequestMessage receivedMsg = new AddingRequestMessage("Delivery Tester", "Delivery Tester", bloodUnit);
                        CustomerRequestMessage request = deliveryTester.testBloodUnit(receivedMsg, testPassed);
                        if (request != null) {
                            out.writeObject(request);
                            out.flush();
                            System.out.println("Blood test failed. Customer request sent to Preservation Officer.");
                        } else {
                            System.out.println("Blood test passed. Ready for delivery.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error testing blood unit: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.print("Enter Blood Group for Customer Request (e.g., A+, O-): ");
                    bloodGroup = sc.nextLine().trim().toUpperCase();
                    try {
                        CustomerRequestMessage request = new CustomerRequestMessage("Delivery Tester", "Preservation Officer", bloodGroup);
                        out.writeObject(request);
                        out.flush();
                        System.out.println("Customer request for " + bloodGroup + " sent to Preservation Officer.");
                    } catch (IOException e) {
                        System.out.println("Error sending customer request: " + e.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Enter Testing Kits Used: ");
                    try {
                        int testingKits = Integer.parseInt(sc.nextLine().trim());
                        KitManagement kit = new KitManagement(0, 0, 0, 0); // Placeholder
                        deliveryTester.kitInfoUpdater(testingKits, kit);
                        // Notify server to update KitManagement
                        BaseMessage kitUpdate = new BaseMessage() {
                            @Override
                            public String getSenderRole() {
                                return "Delivery Tester";
                            }
                            @Override
                            public String getReceiverRole() {
                                return "Server";
                            }
                            @Override
                            public String toString() {
                                return "KitUpdate:0,0,0," + testingKits;
                            }
                        };
                        out.writeObject(kitUpdate);
                        out.flush();
                        System.out.println("Kit usage updated and server notified.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid number.");
                    } catch (IOException e) {
                        System.out.println("Error updating kit usage: " + e.getMessage());
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
}*/

package Client;

import Message.*;
import StuffRelated.*;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class DeliveryTesterClient {

    private static AddingRequestMessage pending = null;

    public static void receiveBloodUnit(AddingRequestMessage msg){
        pending = msg;
        System.out.println("[MSG] Unit queued for test: "+msg.getBloodUnit());
    }

    public static void run(Stuff self,ObjectOutputStream out){
        Scanner sc=new Scanner(System.in);
        DeliveryTester dt=(DeliveryTester) self;

        while(true){
            System.out.println("\nDelivery Menu: 1.Profile  2.Test Pending  0.Logout");
            switch(sc.nextLine().trim()){
                case "1" -> dt.profilePresenter();

                case "2" -> {
                    if(pending==null){System.out.println("No unit.");break;}
                    System.out.print("Pass (true/false): ");
                    boolean pass=Boolean.parseBoolean(sc.nextLine().trim());
                    try{
                        if(!pass){
                            out.writeObject(new CustomerRequestMessage(
                                    "Delivery Tester","Preservation Officer",
                                    pending.getBloodUnit().getBloodGroup()));
                        }
                        else {
                            out.writeObject(new CustomerRequestMessage("Delivery Tester", "Server", "++"));
                        }
                        out.flush();
                        pending=null;
                    }catch(Exception e){System.out.println("Send err:"+e.getMessage());}
                }

                case "0" -> { return; }
            }
        }
    }
}
