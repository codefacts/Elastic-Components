package elasta.orm.exceptions;

/**
 * Created by Jango on 9/14/2016.
 */
public class NoResultException extends RuntimeException {
    public NoResultException(String message) {
        super(message);
    }
}
