package Message;

import java.io.Serializable;

public interface BaseMessage extends Serializable {
    String getSenderRole();
    String getReceiverRole();
}
