package StuffRelated;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class StuffList implements Serializable {
    private List<Stuff> stuff = new ArrayList<>();

    public void loadFromFile(String filename) {
        stuff.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int i = 0;
            while ((line = br.readLine()) != null && i < 5) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String name = parts[0];
                    String id = parts[1];
                    String phone = parts[2];
                    String pin = parts[3];

                    Stuff s = null;
                    if (i == 0) {
                        s = new Manager(name, id, phone, pin);
                    } else if (i == 1) {
                        s = new RegTester(name, id, phone, pin);
                    } else if (i == 2) {
                        s = new DonationOfficer(name, id, phone, pin);
                    } else if (i == 3) {
                        s = new PreservationOfficer(name, id, phone, pin);
                    } else if (i == 4) {
                        s = new DeliveryTester(name, id, phone, pin);
                    }
                    if (s != null) stuff.add(s);
                    i++;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading staff from file: " + e.getMessage());
        }
    }


    public Stuff getById(String id) {
        for (Stuff s : stuff) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public List<Stuff> getAll() {
        return stuff;
    }

    public void printAll() {
        for (Stuff s : stuff) {
            System.out.println(s);
        }
    }

    public Stuff getStuffByRole(String role) {
        for(int i=0; i<stuff.size(); i++)
        {
            if(stuff.get(i).getRole() == role)
                return stuff.get(i);
        }
        return null;
    }
}
