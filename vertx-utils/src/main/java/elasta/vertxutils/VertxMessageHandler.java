package elasta.vertxutils;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.MultiMap;

/**
 * Created by Jango on 9/11/2016.
 */
public interface VertxMessageHandler<T, R> {
    Promise<R> handle(T body, MultiMap headers, String address, String replyAddress) throws Throwable;
}
