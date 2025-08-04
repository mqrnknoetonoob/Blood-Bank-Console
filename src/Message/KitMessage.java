package Message;
/// From server to manager
import KitRelated.KitManagement;

//import java.io.Serializable;

public class KitMessage implements BaseMessage {
    public String Message = "";
    private static final String newLine = System.lineSeparator();
    public KitMessage(KitManagement km)
    {
        if(km.getNumberOfBag() == 0)
            Message += "No bag left.";
        if(km.getNumberOfNeedle() == 0)
        {
            Message += newLine + "No needle left.";
        }
        if (km.getNumberOfPipe() == 0)
        {
            Message += "No pipe left.";
        }
        if (km.getNumberOfTestingKit() == 1)
        {
            Message += "No testing kit left.";
        }
    }

    @Override
    public String getSenderRole() {
        return "Server"; // or dynamically set
    }

    @Override
    public String getReceiverRole() {
        return "Manager"; // or dynamically set
    }
}
