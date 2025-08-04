package StuffRelated;

import BloodRelated.BloodUnit;
import BloodRelated.BloodUnitList;
import KitRelated.KitManagement;
import Message.AddingRequestMessage;

import java.io.Serializable;
import java.util.ArrayList;

public class DonationOfficer extends Stuff{

    public DonationOfficer(String name, String id, String contact, String pin) {
        super(name, id, contact, pin);
        setRole("Donation Officer");
    }

    public void kitInfoUpdater(int usedPipe, int usedBag, KitManagement x) {
        //return new KitManagement(x.getNumberOfBag() - usedBag, x.getNumberOfNeedle(),x.getNumberOfPipe() - usedPipe, x.getNumberOfTestingKit());
        x.setNumberOfBag(x.getNumberOfBag() - usedBag);
        x.setNumberOfPipe(x.getNumberOfPipe() - usedPipe);
        x.saveToFile();
    }

    public void profileShower() {
        showInfo();
    }

    public AddingRequestMessage enqueueApproval(boolean approved, BloodUnit bloodUnit)
    {
        if(approved)
            return new AddingRequestMessage("Donation Officer", "Preservation Officer", bloodUnit);
        /// will be sent to preservation officer
        return null;   ///request cancelled
    }
}

