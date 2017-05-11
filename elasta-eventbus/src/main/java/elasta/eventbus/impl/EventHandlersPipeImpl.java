package elasta.eventbus.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.MapPHandler;
import elasta.core.promise.intfs.Promise;
import elasta.eventbus.EventHandlersPipe;
import elasta.eventbus.ProcessorP;
import elasta.eventbus.ex.EventHandlersPipeException;
import io.vertx.core.eventbus.Message;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/9/2017.
 */
final public class EventHandlersPipeImpl<T, R> implements EventHandlersPipe<T, R> {
    final List<ProcessorP> processorPList;

    public EventHandlersPipeImpl(List<ProcessorP> processorPList) {
        Objects.requireNonNull(processorPList);
        this.processorPList = checkValidity(processorPList);
    }

    private List<ProcessorP> checkValidity(List<ProcessorP> processorPList) {
//        if (processorPList.isEmpty()) {
//            throw new EventHandlersPipeException("ProcessorPList must not be empty");
//        }
        return processorPList;
    }

    @Override
    public Promise<Message<R>> apply(Message<T> message) {
        try {
            Promise<Message> promise = Promises.of(message);
            for (ProcessorP processorP : processorPList) {
                promise = promise.mapP(applyMsg(processorP));
            }
            return cast(promise);
        } catch (Exception ex) {
            return Promises.error(ex, message);
        }
    }

    private <T, R> MapPHandler<Message<T>, Message<R>> applyMsg(ProcessorP<T, R> processorP) {
        return processorP::apply;
    }

    private Promise<Message<R>> cast(Promise promise) {
        return promise;
    }
}
