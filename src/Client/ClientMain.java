

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
