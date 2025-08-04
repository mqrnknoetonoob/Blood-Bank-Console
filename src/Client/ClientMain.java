/*package Client;

import BloodRelated.BloodUnitList;
import KitRelated.KitManagement;
import Message.*;
import StuffRelated.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {

    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
             Scanner sc = new Scanner(System.in)) {

            // --- BLOCK UNTIL SUCCESSFUL LOGIN ---
            Stuff currentUser = login(sc, input, output);

            // --- START LISTENER AFTER LOGIN ---
            Thread listener = new Thread(() -> listenForMessages(input));
            listener.setDaemon(true);
            listener.start();

            // --- LAUNCH ROLE‑SPECIFIC MENU ---
            ClientMenuHandler.handleMenu(currentUser.getRole(), currentUser, output, input);

        } catch (Exception e) {
            System.out.println("Error in client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles role & PIN prompt and performs the LOGIN_REQUEST handshake
     * until the server returns a Stuff object.
     */
/*
    private static Stuff login(Scanner sc, ObjectInputStream input, ObjectOutputStream output)
            throws IOException, ClassNotFoundException {
        Stuff currentUser = null;
        while (currentUser == null) {
            System.out.println("\nSelect Role:");
            System.out.println("1. Manager");
            System.out.println("2. Registration Tester");
            System.out.println("3. Donation Officer");
            System.out.println("4. Preservation Officer");
            System.out.println("5. Delivery Tester");
            System.out.print("Enter role number (1‑5): ");

            int roleIndex;
            try {
                roleIndex = Integer.parseInt(sc.nextLine().trim());
                if (roleIndex < 1 || roleIndex > 5) {
                    System.out.println("Invalid choice. Try again.");
                    continue;
                }
            } catch (NumberFormatException ex) {
                System.out.println("Invalid input. Try again.");
                continue;
            }

            System.out.print("Enter PIN: ");
            String pin = sc.nextLine().trim();

            output.writeObject("LOGIN_REQUEST");
            output.writeObject(roleIndex);  // server expects int index
            output.writeObject(pin);
            output.flush();

            Object response = input.readObject();
            if (response instanceof Stuff stuff) {
                currentUser = stuff;
                System.out.println("Login successful as " + stuff.getRole() + "!\n");
            } else {
                System.out.println("Login failed: " + response + ". Try again.\n");
            }
        }
        return currentUser;
    }

    /**
     * Continuous listener for asynchronous messages from the server.
     * Runs on a daemon thread.
     */
/*    private static void listenForMessages(ObjectInputStream input) {
        try {
            while (true) {
                Object obj = input.readObject();

                if (obj instanceof StockAlertMessage alert) {
                    System.out.println("[ALERT] " + alert.getMessage());
                }
                else if (obj instanceof AddingRequestMessage msg) {
                    if ("Donation Officer".equals(msg.getReceiverRole())) {
                        DonationOfficerClient.receiveBloodUnit(msg);
                    }
                }

                else if (obj instanceof KitMessage kitAlert) {
                    System.out.println("[KIT ALERT] " + kitAlert.Message);
                } else if (obj instanceof AddingRequestMessage addReq) {
                    System.out.println("[MSG] Received Blood Unit from " + addReq.getSenderRole() + ": " + addReq.getBloodUnit());
                } else if (obj instanceof CustomerRequestMessage custReq) {
                    System.out.println("[MSG] Customer Request from " + custReq.getSenderRole() + " for group " + custReq.getBloodGroup());
                } else if (obj instanceof StuffList staffList) {
                    System.out.println("===== Staff Info =====");
                    staffList.printAll();
                } else if (obj instanceof KitManagement kit) {
                    System.out.println("===== Kit Quantities =====");
                    System.out.println("Bags=" + kit.getNumberOfBag() +
                            ", Needles=" + kit.getNumberOfNeedle() +
                            ", Pipes=" + kit.getNumberOfPipe() +
                            ", TestingKits=" + kit.getNumberOfTestingKit());
                } else if (obj instanceof BloodUnitList bloodList) {
                    System.out.println("===== Blood Unit Details =====");
                    bloodList.printAll();
                } else if (obj instanceof String str) {
                    System.out.println("[SERVER] " + str);
                } else {
                    System.out.println("[INFO] Received unknown object: " + obj);
                }
            }
        } catch (EOFException eof) {
            System.out.println("Server closed connection.");
        } catch (Exception e) {
            System.out.println("Error receiving messages: " + e.getMessage());
        }
    }
}
*/

package Client;

import Message.*;
import StuffRelated.*;
import BloodRelated.*;
import KitRelated.*;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientMain {
    private static final String HOST = "localhost";
    private static final int PORT    = 12345;

    public static void main(String[] args) {
        try (Socket sock = new Socket(HOST, PORT);
             ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
             ObjectInputStream  in  = new ObjectInputStream(sock.getInputStream());
             Scanner scan          = new Scanner(System.in))
        {
            /* ---------- LOGIN ---------- */
            Stuff me = null;
            while (me == null) {
                int roleIdx = promptRole(scan);
                System.out.print("Enter PIN: ");
                String pin = scan.nextLine().trim();

                out.writeObject("LOGIN_REQUEST");
                out.writeObject(roleIdx);
                out.writeObject(pin);
                out.flush();

                Object resp = in.readObject();
                if (resp instanceof Stuff s) {
                    me = s;
                    System.out.println("Login successful as " + s.getRole());
                    out.writeObject(s.getRole());          // announce role
                    out.flush();
                } else {
                    System.out.println("Login failed: " + resp);
                }
            }

            /* ---------- LISTENER THREAD ---------- */
            Thread listener = new Thread(() -> listen(in));
            listener.setDaemon(true);
            listener.start();

            /* ---------- ROLE‑SPECIFIC UI ---------- */
            switch (me.getRole()) {
                case "Manager"             -> ManagerClient.run(me, out);
                case "Registration Tester" -> RegTesterClient.run(me, out);
                case "Donation Officer"    -> DonationOfficerClient.run(me, out);
                case "Preservation Officer"-> PreservationOfficerClient.run(me, out);
                case "Delivery Tester"     -> DeliveryTesterClient.run(me, out);
            }
        }
        catch (Exception e) { System.out.println("Client error: " + e.getMessage()); }
    }

    private static int promptRole(Scanner sc) {
        System.out.println("\n1.Manager  2.RegTester  3.Donation  4.Preservation  5.Delivery");
        while (true) {
            try { System.out.print("Select role (1‑5): ");
                int x = Integer.parseInt(sc.nextLine().trim());
                if (x>=1 && x<=5) return x; }
            catch (NumberFormatException ignored) {}
        }
    }

    /* ---------- LISTENER DISPATCH ---------- */
    private static void listen(ObjectInputStream in) {
        try {
            while (true) {
                Object o = in.readObject();

                if (o instanceof AddingRequestMessage add) {
                    switch (add.getReceiverRole()) {
                        case "Donation Officer"     -> DonationOfficerClient.receiveBloodUnit(add);
                        case "Preservation Officer" -> PreservationOfficerClient.receiveBloodUnit(add);
                        case "Delivery Tester"      -> DeliveryTesterClient.receiveBloodUnit(add);
                        default -> System.out.println("[MSG] BloodUnit from " + add.getSenderRole());
                    }
                }
                else if (o instanceof CustomerRequestMessage req &&
                        "Preservation Officer".equals(req.getReceiverRole()))
                    PreservationOfficerClient.receiveCustomerRequest(req);

                else if (o instanceof StockAlertMessage al) System.out.println("[ALERT] "+al.getMessage());
                else if (o instanceof KitMessage km)        System.out.println("[KIT] "+km.Message);
                else if (o instanceof StuffList sl)         { System.out.println("===== Staff ====="); sl.printAll(); }
                else if (o instanceof KitManagement kit)    { System.out.println("Bags="+kit.getNumberOfBag()); }
                else if (o instanceof BloodUnitList bl)     { System.out.println("===== Blood ====="); bl.printAll(); }
                else if (o instanceof String s)             System.out.println("[SERVER] "+s);
            }
        } catch (Exception e) { System.out.println("Listener stopped: "+e.getMessage()); }
    }
}
