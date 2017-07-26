package elasta.orm.query.read.builder.ex;

/**
 * Created by sohan on 2017-07-25.
 */
final public class InvalidPrimaryKeyIndexException extends RuntimeException {
    public InvalidPrimaryKeyIndexException(String msg) {
        super(msg);
    }
}
