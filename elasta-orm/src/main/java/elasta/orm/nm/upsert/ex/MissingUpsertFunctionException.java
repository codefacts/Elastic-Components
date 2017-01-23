package elasta.orm.nm.upsert.ex;

/**
 * Created by Jango on 2017-01-23.
 */
public class MissingUpsertFunctionException extends RuntimeException {
    public MissingUpsertFunctionException(String msg) {
        super(msg);
    }
}
