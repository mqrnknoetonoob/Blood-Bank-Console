package Client;

import BloodRelated.BloodUnitList;
import KitRelated.*;
import StuffRelated.*;
import Message.*;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.Scanner;


public class ManagerClient {
    public static void run(Stuff currentStuff, ObjectOutputStream out) {
        Scanner sc = new Scanner(System.in);
        Manager manager = (Manager) currentStuff;

        while (true) {
            System.out.println("\nManager Menu:");
            System.out.println("1. Send blood request");
            System.out.println("2. View all staff info");
            System.out.println("3. View all kit quantities");
            System.out.println("4. View blood unit details");
            System.out.println("5. Receive kit supplies");
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
                    System.out.print("Enter blood group (e.g., A+, O-): ");
                    String bloodGroup = sc.nextLine().trim().toUpperCase();
                    try {
                        CustomerRequestMessage request = manager.CustomerRequest(bloodGroup);
                        out.writeObject(request);
                        out.flush();
                        System.out.println("Blood request for " + bloodGroup + " sent to Preservation Officer.");
                    } catch (IOException e) {
                        System.out.println("Error sending blood request: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        BaseMessage staffRequest = new BaseMessage() {
                            @Override
                            public String getSenderRole() {
                                return "Manager";
                            }
                            @Override
                            public String getReceiverRole() {
                                return "Server";
                            }
                            @Override
                            public String toString() {
                                return "StaffInfoRequest";
                            }
                        };
                        out.writeObject(staffRequest);
                        out.flush();
                        System.out.println("Staff info request sent to server.");
                    } catch (IOException e) {
                        System.out.println("Error sending staff info request: " + e.getMessage());
                    }
                    break;
                case 3:
                    /*try {
                        BaseMessage kitRequest = new BaseMessage() {
                            @Override
                            public String getSenderRole() {
                                return "Manager";
                            }
                            @Override
                            public String getReceiverRole() {
                                return "Server";
                            }
                            @Override
                            public String toString() {
                                return "KitQuantityRequest";
                            }
                        };
                        out.writeObject(kitRequest);
                        out.flush();
                        System.out.println("Kit quantity request sent to server.");
                    } catch (IOException e) {
                        System.out.println("Error sending kit quantity request: " + e.getMessage());
                    }*/
                    KitManagement kitManagement = new KitManagement();
                    kitManagement.showInfo();
                    break;
                case 4:
                    /*try {
                        BaseMessage bloodRequest = new BaseMessage() {
                            @Override
                            public String getSenderRole() {
                                return "Manager";
                            }
                            @Override
                            public String getReceiverRole() {
                                return "Server";
                            }
                            @Override
                            public String toString() {
                                return "BloodUnitDetailsRequest";
                            }
                        };
                        out.writeObject(bloodRequest);
                        out.flush();
                        System.out.println("Blood unit details request sent to server.");
                    } catch (IOException e) {
                        System.out.println("Error sending blood unit details request: " + e.getMessage());
                    }*/
                    BloodUnitList x = new BloodUnitList();
                    x.printAll();
                    break;
                case 5:
                    System.out.println("Enter quantities to receive:");
                    try {
                        System.out.print("Bags: ");
                        int bags = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Needles: ");
                        int needles = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Pipes: ");
                        int pipes = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Testing Kits: ");
                        int testingKits = Integer.parseInt(sc.nextLine().trim());
                        KitManagement kit = new KitManagement(); // Placeholder for server-side update
                        manager.receiveKit(kit, bags, needles, pipes, testingKits);
                        // Notify server to update KitManagement
                        BaseMessage kitUpdate = new BaseMessage() {
                            @Override
                            public String getSenderRole() {
                                return "Manager";
                            }
                            @Override
                            public String getReceiverRole() {
                                return "Server";
                            }
                            @Override
                            public String toString() {
                                return "KitUpdate:" + bags + "," + needles + "," + pipes + "," + testingKits;
                            }
                        };
                        out.writeObject(kitUpdate);
                        out.flush();
                        System.out.println("Kit supplies received and server notified.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter valid numbers.");
                    } catch (IOException e) {
                        System.out.println("Error updating kit supplies: " + e.getMessage());
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
