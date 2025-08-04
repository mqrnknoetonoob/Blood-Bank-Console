package Message;

import BloodRelated.BloodUnit;

import java.io.Serializable;

public class AddingRequestMessage implements BaseMessage {
    private String from;
    private String to;
    private BloodUnit bloodUnit;
    public AddingRequestMessage(String from, String to, BloodUnit bloodUnit) {
        this.bloodUnit = bloodUnit;
        this.from = from;
        this.to = to;
    }
    public BloodUnit getBloodUnit() {
        return bloodUnit;
    }
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
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
