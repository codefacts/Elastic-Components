package elasta.orm.exceptions;

/**
 * Created by Jango on 9/16/2016.
 */
public class SqlParameterException extends RuntimeException {
    public SqlParameterException(String msg) {
        super(msg);
    }
}
