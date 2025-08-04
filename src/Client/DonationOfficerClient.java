package Client;

import BloodRelated.*;
import KitRelated.*;
import StuffRelated.*;
import Message.*;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class DonationOfficerClient {
    private static final Queue<AddingRequestMessage> pendingBloodUnits = new LinkedList<>();

    public static void receiveBloodUnit(AddingRequestMessage msg) {
        pendingBloodUnits.offer(msg);
        System.out.println("[MSG] Received Blood Unit from " + msg.getSenderRole());
    }

    public static void run(Stuff currentStuff, ObjectOutputStream out) {
        Scanner sc = new Scanner(System.in);
        DonationOfficer donationOfficer = (DonationOfficer) currentStuff;

        while (true) {
            System.out.println("\nDonation Officer Menu:");
            System.out.println("1. View Profile");
            System.out.println("2. Approve Next Donor Blood Unit");
            System.out.println("3. Update Kit Usage");
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
                    donationOfficer.profileShower();
                    break;
                case 2:
                    if (pendingBloodUnits.isEmpty()) {
                        System.out.println("No pending blood units to approve.");
                        break;
                    }
                    AddingRequestMessage pending = pendingBloodUnits.poll();
                    BloodUnit unit = pending.getBloodUnit();
                    System.out.println("Approve the following Blood Unit?");
                    System.out.println(unit);
                    System.out.print("Approve (true/false): ");
                    boolean approved;
                    try {
                        approved = Boolean.parseBoolean(sc.nextLine().trim());
                    } catch (Exception e) {
                        System.out.println("Invalid input. Please enter true or false.");
                        pendingBloodUnits.offer(pending); // put back to queue
                        break;
                    }

                    try {
                        AddingRequestMessage msg = donationOfficer.enqueueApproval(approved, unit);
                        if (msg != null) {
                            out.writeObject(msg);
                            out.flush();
                            System.out.println("Blood unit approved. Message forwarded to Preservation Officer.");
                        } else {
                            System.out.println("Blood unit not approved. Discarded.");
                        }
                    } catch (IOException e) {
                        System.out.println("Error forwarding blood unit: " + e.getMessage());
                    }
                    /*break;
                case 3:*/
                    System.out.println("Enter Kit Usage:");
                    try {
                        System.out.print("Pipes used: ");
                        int pipes = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Bags used: ");
                        int bags = Integer.parseInt(sc.nextLine().trim());
                        KitManagement kit = new KitManagement(); // Placeholder
                        donationOfficer.kitInfoUpdater(pipes, bags, kit);
                        BaseMessage kitUpdate = new BaseMessage() {
                            @Override
                            public String getSenderRole() { return "Donation Officer"; }
                            @Override
                            public String getReceiverRole() { return "Server"; }
                            @Override
                            public String toString() {
                                return "KitUpdate:0," + bags + "," + pipes + ",0";
                            }
                        };
                        out.writeObject(kitUpdate);
                        out.flush();
                        System.out.println("Kit usage updated and server notified.");
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter valid numbers.");
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
}

