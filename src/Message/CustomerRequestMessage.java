package Message;
public class CustomerRequestMessage implements BaseMessage {
    private String from;
    private String to;
    private String bloodGroup;
    public CustomerRequestMessage(String from, String to, String bloodGroup) {
        this.from = from;
        this.to = to;
        this.bloodGroup = bloodGroup;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getBloodGroup() {
        return bloodGroup;
    }
    @Override
    public String getSenderRole() {
        return from; // or dynamically set
    }

    @Override
    public String getReceiverRole() {
        return to; // or dynamically set
    }
}
