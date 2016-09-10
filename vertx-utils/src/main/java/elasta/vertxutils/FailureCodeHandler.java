package elasta.vertxutils;

/**
 * Created by Jango on 9/11/2016.
 */
public interface FailureCodeHandler {
    FailureTuple handleFailure(Throwable throwable);
}
