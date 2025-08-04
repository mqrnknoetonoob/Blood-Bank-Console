package StuffRelated;

import BloodRelated.BloodUnit;
import BloodRelated.BloodUnitList;
import BloodRelated.Person;
import KitRelated.KitManagement;
import Message.AddingRequestMessage;

import java.io.Serializable;
import java.time.LocalDate;

public class RegTester extends Stuff{
    private Person donor;
    private static final long serialVersionUID = 1L;

    public RegTester(String name, String id, String contact, String pin) {
        super(name, id, contact, pin);  // uses existing constructor
        donor = null;
        setRole("Registration Tester");
    }

    public Person registerDonor(String name, String id, String bg) {
        donor = new Person(name, id, bg);
        return donor;
    }

    public AddingRequestMessage checkHealth(boolean x, Person person) {
        if(x) /// switch
        {
            LocalDate today = LocalDate.now();
            LocalDate expiry = today.plusDays(14);
            BloodUnit q = new BloodUnit(person.getName(), person.getNID(), person.getBloodGroup(), today, expiry);
            return new AddingRequestMessage("Registration Tester","Donation Officer",q);
        }
        return null;
    }
    public void kitInfoUpdater(int usedTestingKit, KitManagement x) {
        //return new KitManagement(x.getNumberOfBag() - usedBag, x.getNumberOfNeedle(),x.getNumberOfPipe() - usedPipe, x.getNumberOfTestingKit());
        x.setNumberOfTestingKit(x.getNumberOfTestingKit() - usedTestingKit);
        x.saveToFile();
    }
}


