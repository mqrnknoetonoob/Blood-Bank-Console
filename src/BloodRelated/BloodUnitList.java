
package BloodRelated;
//import BloodRelated.BloodUnit;

import Message.StockAlertMessage;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class BloodUnitList implements Serializable {

    private ArrayList<BloodUnit> Apos = new ArrayList<>();
    private ArrayList<BloodUnit> Aneg = new ArrayList<>();
    private ArrayList<BloodUnit> Bpos = new ArrayList<>();
    private ArrayList<BloodUnit> Bneg = new ArrayList<>();
    private ArrayList<BloodUnit> ABpos = new ArrayList<>();
    private ArrayList<BloodUnit> ABneg = new ArrayList<>();
    private ArrayList<BloodUnit> Opos = new ArrayList<>();
    private ArrayList<BloodUnit> Oneg = new ArrayList<>();

    public BloodUnitList() {
        loadAllFromFiles("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data");
    }

    // fetch list by blood group
    public ArrayList<BloodUnit> fetchBlood(String group) {
        group = group.toUpperCase();
        switch (group) {
            case "A+": return Apos;
            case "A-": return Aneg;
            case "B+": return Bpos;
            case "B-": return Bneg;
            case "AB+": return ABpos;
            case "AB-": return ABneg;
            case "O+": return Opos;
            case "O-": return Oneg;
            default: return null;
        }
    }

    // add a unit
    public void append(ArrayList<BloodUnit> list, BloodUnit unit) {
        list.add(unit);
        this.saveAllToFiles("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data");
    }

    // remove by index
    public void leave(ArrayList<BloodUnit> list, int index) {
        list.remove(index);
        this.saveAllToFiles("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data");
    }

    // remove from front (queue)
    public BloodUnit dequeue(ArrayList<BloodUnit> list) {
        if (list == null || list.isEmpty())
        {
            return null;
        }
        BloodUnit bloodUnit = list.remove(0);
        this.saveAllToFiles("C:\\Users\\win11\\OneDrive\\Documents\\BUET program\\project java\\javaProject\\data");
        return bloodUnit;
    }

    public void printAll() {
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : groups) {
            ArrayList<BloodUnit> list = fetchBlood(group);
            System.out.println("Group " + group + ":");
            for (BloodUnit b : list) {
                System.out.println("  " + b);
            }
        }
    }

    public ArrayList<BloodUnit> getExpiredUnits() {
        ArrayList<BloodUnit> expired = new ArrayList<>();
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : groups) {
            ArrayList<BloodUnit> list = fetchBlood(group);
            for (BloodUnit b : list) {
                if (b.isExpired()) expired.add(b);
            }
        }
        return expired;
    }

    public void saveAllToFiles(String folderPath) {
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        for (String group : bloodGroups) {
            try {
                File file = new File(folderPath + "/blood_" + group.replace("+", "pos").replace("-", "neg") + ".csv");
                BufferedWriter bw = new BufferedWriter(new FileWriter(file));

                for (BloodUnit unit : fetchBlood(group)) {
                    String line = unit.getDonorName() + "," + unit.getDonorNID() + "," +
                            unit.getBloodGroup() + "," + unit.getDonationDate() + "," + unit.getExpiryDate();
                    bw.write(line);
                    bw.newLine();
                }
                bw.close();
            } catch (IOException e) {
                System.err.println("Error writing to file for group: " + group);
            }
        }
    }


    public void loadAllFromFiles(String folderPath) {
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        Apos.clear();  Aneg.clear();
        Bpos.clear();  Bneg.clear();
        ABpos.clear(); ABneg.clear();
        Opos.clear();  Oneg.clear();

        for (String group : bloodGroups) {
            String fileName = folderPath + "/blood_" + group.replace("+", "pos").replace("-", "neg") + ".csv";
            ArrayList<BloodUnit> list = fetchBlood(group);
            try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",", -1);
                    if (parts.length == 5) {
                        String donor = parts[0];
                        String nid = parts[1];
                        String grp = parts[2];
                        LocalDate donationDate = LocalDate.parse(parts[3]);
                        LocalDate expiryDate = LocalDate.parse(parts[4]);
                        BloodUnit unit = new BloodUnit(donor, nid, grp, donationDate, expiryDate);
                        list.add(unit);
                    }
                }
                //System.out.println("Blood data loaded successfully");
            } catch (IOException e) {
                System.err.println("Error loading blood units from file: " + fileName);
            }
        }
    }
}

