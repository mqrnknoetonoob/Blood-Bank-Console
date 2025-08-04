/*package StuffRelated;

import BloodRelated.BloodUnit;
import BloodRelated.BloodUnitList;
import KitRelated.KitManagement;

import java.util.*;
public class PreservationOfficer extends Stuff {

    private BloodUnitList bloodList;
    private KitManagement kit;
    private Queue<String> customerRequest;
    public PreservationOfficer(String name,String id,String contact,String pin,KitManagement kit){
       super(name,id,contact,pin,false);
       this.kit=kit;
       this.bloodList=new BloodUnitList();
       this.customerRequest=new LinkedList<>();
    }
   public void receiveCustomerRequest(String bloodGroup){
       customerRequest.add(bloodGroup.toUpperCase());
       System.out.println("Customer request received for blood group: " + bloodGroup);
   }
    public void completeCustomerRequest(String bloodGroup) {
        ArrayList<BloodUnit> list = bloodList.fetchBlood(bloodGroup);
        if (list != null && !list.isEmpty()) {
            BloodUnit unit = bloodList.dequeue(list);
            System.out.println("Blood request for " + bloodGroup + " fulfilled.");
            unit.showInfo();
        } else {
            System.out.println("No available unit for blood group: " + bloodGroup);
        }
    }
    public void receiveDonatedBlood(BloodUnit x) {
        ArrayList<BloodUnit> list = bloodList.fetchBlood(x.getBloodGroup());
        if (list != null) {
            bloodList.append(list, x);
            System.out.println("Donated blood added: " + x.getBloodGroup() + ", Donor: " + x.getName());
        }
    }
    public void eliminateExpired() {
        int count = 0;
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        for (String group : groups) {
            ArrayList<BloodUnit> list = bloodList.fetchBlood(group);
            for (int i = 0; i < list.size(); ) {
                if (isExpired(list.get(i).getExpiryDate())) {
                    bloodList.leave(list, i);
                    count++;
                } else {
                    i++;
                }
            }
        }
        System.out.println("Expired blood units removed: " + count);
    }
    public void expirationMessage() {
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : groups) {
            ArrayList<BloodUnit> list = bloodList.fetchBlood(group);
            for (BloodUnit unit : list) {
                if (isExpired(unit.getExpiryDate())) {
                    System.out.println("Expired Blood: " + unit.getBloodGroup() + " | Donor: " + unit.getName() + " | Expiry: " + unit.getExpiryDate());
                }
            }
        }
    }
    public void bagInfoUpdate() {
        kit.setNumberOfBag(kit.getNumberOfBag() + 5); // demo logic
        System.out.println("Bag count updated. Total now: " + kit.getNumberOfBag());
    }
    public void ProfileShower() {
        this.showInfo();
        System.out.println("Permission: " + PERMISSION);
        System.out.println("Current Blood Stock:");
        showAllStock();
    }
    private boolean isExpired(String expiryDate) {
        try {
            String[] parts = expiryDate.split("-");
            Calendar cal = Calendar.getInstance();
            int today = Integer.parseInt("" + cal.get(Calendar.YEAR) + String.format("%02d", cal.get(Calendar.MONTH)+1) + String.format("%02d", cal.get(Calendar.DAY_OF_MONTH)));
            int exd = Integer.parseInt(parts[0] + parts[1] + parts[2]); // yyyyMMdd
            return exd < today;
        } catch (Exception e) {
            return false; // if parsing fails
        }
    }
    private void showAllStock() {
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : groups) {
            ArrayList<BloodUnit> list = bloodList.fetchBlood(group);
            System.out.println(group + ": " + list.size() + " units");
        }
    }
}
*/

package StuffRelated;

import BloodRelated.BloodUnit;
import BloodRelated.BloodUnitList;
import Message.AddingRequestMessage;
import Message.BaseMessage;
import Message.CustomerRequestMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PreservationOfficer extends Stuff{

    public PreservationOfficer(String name, String id, String phone, String pin) {
        super(name, id, phone, pin);
        setRole("Preservation Officer");
    }

    public void checkExpiredUnits(BloodUnitList bloodList) {
        List<BloodUnit> expired = bloodList.getExpiredUnits();
        if (expired.isEmpty()) {
            System.out.println(" No expired blood units.");
        } else {
            System.out.println("️ Expired blood units:");
            for (BloodUnit unit : expired) {
                System.out.println(unit);
            }
        }
    }
    public void enqueueBloodUnit(AddingRequestMessage bloodUnit, BloodUnitList bloodUnitList,ObjectOutputStream out) throws IOException {
        ArrayList<BloodUnit> groupList = bloodUnitList.fetchBlood(bloodUnit.getBloodUnit().getBloodGroup());
        BloodUnit blood = bloodUnit.getBloodUnit();
        if (groupList != null) {
            groupList.add(blood);
            bloodUnitList.saveAllToFiles("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data");
            /// ekhane ekta message server e pathaite chai, CustomerRequestMessage("Preservation Officer", "Server", "++");
            BaseMessage update = new BaseMessage() {
                public String getSenderRole()   { return "Preservation Officer"; }
                public String getReceiverRole() { return "Server"; }
                public String toString()        {
                    return "StockUpdate:" + bloodUnit.getBloodUnit().getBloodGroup();
                }
            };
            try { out.writeObject(update); out.flush(); }
            catch (IOException e){ System.out.println("Send err: "+e.getMessage()); }
            System.out.println("Blood unit added for group " + blood.getBloodGroup());
        } else {
            System.out.println("Blood group list not found.");
        }
    }
    public AddingRequestMessage dequeueBloodUnit(CustomerRequestMessage bloodUnit, BloodUnitList bloodUnitList) {
        ArrayList<BloodUnit> x = bloodUnitList.fetchBlood(bloodUnit.getBloodGroup());
        BloodUnit y = bloodUnitList.dequeue(x);
        bloodUnitList.saveAllToFiles("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data");
        return new AddingRequestMessage("Preservation Officer","Delivery Tester",y);
        /// will go to delivery tester for
    }

    /*public AddingRequestMessage customerRequest(String bloodGroup, BloodUnitList bloodUnitList) {
        return new AddingRequestMessage("PreservationOfficer","DeliveryTester",
                bloodUnitList.dequeue(bloodUnitList.fetchBlood(bloodGroup)));
        /// will go to delivery tester
    }*/
    public void removeExpiredBloodUnits(BloodUnitList bloodUnitList) {
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : groups) {
            ArrayList<BloodUnit> list = bloodUnitList.fetchBlood(group);
            for (int i = 0; i < list.size(); i++) {
                BloodUnit unit = list.get(i);
                if (unit.isExpired()) {
                    list.remove(i);
                }
                else break;
            }
        }
        // Save changes to files
        bloodUnitList.saveAllToFiles("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data");
        System.out.println("All expired blood units removed successfully.");
    }

    public void removeBloodUnit(BloodUnitList bloodUnitList, String bloodGroup, int[] selectedIDX) {
        ArrayList<BloodUnit> units = bloodUnitList.fetchBlood(bloodGroup);
        for(int i=0; i<selectedIDX.length; i++) {
            bloodUnitList.leave(units, selectedIDX[i]);
            for(int j=i+1; j<selectedIDX.length; j++) {
                if(selectedIDX[j] > selectedIDX[i]) {
                    selectedIDX[j]--;
                }
            }
        }
    }
}
