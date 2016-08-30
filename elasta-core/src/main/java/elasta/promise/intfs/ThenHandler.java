package elasta.promise.intfs;

import elasta.intfs.ConsumerUnchecked;

/**
 * Created by someone on 16/10/2015.
 */
public interface ThenHandler<T> extends ConsumerUnchecked<T>, Invokable {
}
