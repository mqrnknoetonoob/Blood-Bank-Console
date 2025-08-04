package Client;

import StuffRelated.*;
import BloodRelated.*;
import Message.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class PreservationOfficerClient {

    /*  inbound queues  */
    private static final Queue<AddingRequestMessage>    pendingUnits   = new LinkedList<>();
    private static final Queue<CustomerRequestMessage> pendingRequest = new LinkedList<>();

    /* called by ClientMain.listener() */
    public static void receiveBloodUnit(AddingRequestMessage msg) {
        pendingUnits.offer(msg);
        System.out.println("[MSG] Blood Unit queued from " +
                msg.getSenderRole() + " : " + msg.getBloodUnit());
    }
    public static void receiveCustomerRequest(CustomerRequestMessage req) {
        pendingRequest.offer(req);
        System.out.println("[MSG] Customer Request queued: group " + req.getBloodGroup());
    }

    /* ------------- interactive menu ------------- */
    public static void run(Stuff current, ObjectOutputStream out) throws IOException {
        Scanner sc = new Scanner(System.in);
        PreservationOfficer po = (PreservationOfficer) current;
        //BloodUnitList bloodList = new BloodUnitList();   // placeholder; real list kept server‑side

        while (true) {
            System.out.println("\nPreservation Officer Menu:");
            System.out.println("1. View Profile");
            System.out.println("2. Enqueue Next Pending Blood Unit");
            System.out.println("3. Dequeue By Blood Group (manual)");
            System.out.println("4. Remove Expired Units");
            System.out.println("5. Handle Next Customer Request");
            System.out.println("0. Logout");

            int ch;
            try { ch = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Number please."); continue; }

            switch (ch) {
                case 1 -> po.showInfo();

                /* ------- option 2 : take one pending AddingRequestMessage ------- */
                case 2 -> {
                    if (pendingUnits.isEmpty()) {
                        System.out.println("No pending blood units.");
                        break;
                    }
                    AddingRequestMessage add = pendingUnits.poll();
                    BloodUnitList bloodList = new BloodUnitList();
                    po.enqueueBloodUnit(add, bloodList,out);
                    //out.writeObject(add); out.flush();
                    System.out.println("Enqueued & server notified.");
                }

                /* ------- option 3 : manual dequeue by group ------- */
                case 3 -> {
                    System.out.print("Enter group (e.g. A+,O-): ");
                    String grp = sc.nextLine().trim().toUpperCase();
                    CustomerRequestMessage dummy =
                            new CustomerRequestMessage("Manager","Preservation Officer",grp);
                    BloodUnitList bloodList = new BloodUnitList();
                    AddingRequestMessage res = po.dequeueBloodUnit(dummy,bloodList);
                    if (res != null) {
                        try { out.writeObject(res); out.flush(); }
                        catch (IOException e){ System.out.println("Send error: "+e.getMessage());}
                        System.out.println("Sent unit -> Delivery Tester");
                    } else System.out.println("No unit available for "+grp);
                }

                /* ------- option 4 : remove expired ------- */
                case 4 -> {
                    BloodUnitList bloodList = new BloodUnitList();
                    po.removeExpiredBloodUnits(bloodList);
                    System.out.println("Expired units removed (local list).");
                    // server update optional
                }

                /* ------- option 5 : auto‑handle next customer request ------- */
                case 5 -> {
                    if (pendingRequest.isEmpty()) {
                        System.out.println("No pending customer request.");
                        break;
                    }
                    BloodUnitList bloodList = new BloodUnitList();
                    CustomerRequestMessage req = pendingRequest.poll();
                    AddingRequestMessage res = new AddingRequestMessage("Preservation Officer", "Delivery Tester", po.dequeueBloodUnit(req,bloodList).getBloodUnit());
                    if (res.getBloodUnit() != null) {
                        try { out.writeObject(res); out.flush(); }
                        catch (IOException e){ System.out.println("Send error: "+e.getMessage());}
                        System.out.println("Request fulfilled -> Delivery Tester");
                    } else {
                        System.out.println("No unit for group "+ req.getBloodGroup()
                                + "; re‑queueing request.");
                        pendingRequest.offer(req);   // put back for later
                    }
                }
                case 6 -> {
                    Scanner scn = new Scanner(System.in);
                    System.out.print("Enter group (e.g. A+,O-): ");
                    String bg = scn.nextLine();
                    BloodUnitList bloodList = new BloodUnitList();
                    ArrayList<BloodUnit> x = bloodList.fetchBlood(scn.next());
                    System.out.print("Enter the SI no of units: Example: 4,2,8 ");
                    int[] arr;
                    String num = sc.nextLine();
                    String numarr[] = num.split(",");
                    arr = new int[numarr.length];
                    for (int i = 0; i < numarr.length; i++) {
                        arr[i] = Integer.parseInt(numarr[i]);
                    }
                    po.removeBloodUnit(bloodList, bg, arr);
                }

                case 0 -> {
                    try { out.writeObject("LOGOUT"); out.flush(); }
                    catch (IOException ignore) {}
                    System.out.println("Logging out …"); return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}
