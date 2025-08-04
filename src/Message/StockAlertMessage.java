package Message;
/// from server to manager
//import java.io.Serializable;

public class StockAlertMessage implements BaseMessage {
    private String message;

    public StockAlertMessage(String bloodGroup) {
        this.message = "No unit available for blood group: " + bloodGroup;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
    public String getSenderRole() {
        return "Server"; // or dynamically set
    }

    @Override
    public String getReceiverRole() {
        return "Manager"; // or dynamically set
    }
}
