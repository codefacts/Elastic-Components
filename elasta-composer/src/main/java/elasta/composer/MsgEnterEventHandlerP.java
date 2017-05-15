package elasta.composer;

import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;

/**
 * Created by sohan on 5/15/2017.
 */
public interface MsgEnterEventHandlerP<T, R> extends EnterEventHandlerP<Msg<T>, Msg<R>> {
    @Override
    Promise<StateTrigger<Msg<R>>> handle(Msg<T> tMsg) throws Throwable;
}
