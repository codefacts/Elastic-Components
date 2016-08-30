package elasta.core.promise.intfs;

import elasta.core.intfs.ConsumerUnchecked;

/**
 * Created by someone on 16/10/2015.
 */
public interface ErrorHandler extends ConsumerUnchecked<Throwable>, Invokable {
}
