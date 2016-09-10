package elasta.composer;

/**
 * Created by Jango on 9/11/2016.
 */
public enum FailureCodes {
    UNEXPECTED_ERROR(500, "unexpected.error");

    private final int code;
    private final String messageCode;

    FailureCodes(int code, String messageCode) {
        this.code = code;
        this.messageCode = messageCode;
    }

    public int code() {
        return code;
    }

    public String messageCode() {
        return messageCode;
    }
}
