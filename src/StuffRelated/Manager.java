package StuffRelated;

import BloodRelated.BloodUnit;
import BloodRelated.BloodUnitList;
import KitRelated.KitManagement;
import Message.CustomerRequestMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Manager extends Stuff{

    public Manager(String name, String id, String contact, String pin) {
        super(name, id, contact, pin);
        setRole("Manager");
        //login();  // managers are logged in by default
    }

    public void profilePresenter() {
        showInfo();
    }

    public void showList(StuffList x) {
        x.printAll();
    }

    public void receiveKit(KitManagement x, int a, int b, int c, int d) {
        x.setNumberOfBag(x.getNumberOfBag() + a);
        x.setNumberOfNeedle(x.getNumberOfNeedle() + b);
        x.setNumberOfPipe(x.getNumberOfPipe() + c);
        x.setNumberOfTestingKit(x.getNumberOfTestingKit() + d);
        x.saveToFile();
    }

    public void showBloodInfo(BloodUnitList x) {
        String[] groups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        for (String group : groups) {
            List<BloodUnit> list = x.fetchBlood(group);
            for (BloodUnit unit : list) {
                unit.showInfo();
            }
        }
    }

    public CustomerRequestMessage CustomerRequest(String bg)
    {
        return new CustomerRequestMessage("Manager", "Preservation Officer", bg);
        /// Preservation officer will receive message to dequeue
    }
}
