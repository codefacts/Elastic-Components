package elasta.vertxutils;

/**
 * Created by Jango on 9/11/2016.
 */
final public class FailureTuple {
    final int code;
    final String messageCode;

    public FailureTuple(int code, String messageCode) {
        this.code = code;
        this.messageCode = messageCode;
    }

    public int getCode() {
        return code;
    }

    public String getMessageCode() {
        return messageCode;
    }
}
