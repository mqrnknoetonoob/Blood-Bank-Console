
package StuffRelated;
import java.io.Serializable;
import BloodRelated.BloodUnit;
import KitRelated.KitManagement;
import Message.AddingRequestMessage;
import Message.CustomerRequestMessage;

public class DeliveryTester extends Stuff {

    public DeliveryTester(String name, String id, String contact, String pin) {
        super(name, id, contact, pin);
        setRole("Delivery Tester");
    }

    public void profilePresenter() {
        showInfo();
    }

    public void kitInfoUpdater(int usedTestingKit, KitManagement x) {
        //return new KitManagement(x.getNumberOfBag() - usedBag, x.getNumberOfNeedle(),x.getNumberOfPipe() - usedPipe, x.getNumberOfTestingKit());
        x.setNumberOfTestingKit(x.getNumberOfTestingKit() - usedTestingKit);
        x.saveToFile();
    }

    public CustomerRequestMessage testBloodUnit(AddingRequestMessage bloodUnit, boolean passed) {

        if (passed) {
            System.out.println("Blood test passed for donor: " + bloodUnit.getBloodUnit().getBloodGroup());
            return null;
        } else {
            ///System.out.println("Blood test failed. Please provide another unit.");
            /// ekhane CustomerRequestMessage pathabe preservation officer k
            return new CustomerRequestMessage("DeliveryTester", "PreservationOfficer", bloodUnit.getBloodUnit().getBloodGroup());
        }
    }
}
