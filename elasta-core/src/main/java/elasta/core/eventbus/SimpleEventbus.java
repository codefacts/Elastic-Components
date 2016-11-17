package elasta.core.eventbus;

import elasta.core.promise.intfs.Promise;

import java.util.Map;

/**
 * Created by Jango on 11/5/2016.
 */
public interface SimpleEventBus {

    <T, R> SimpleEventBus addProcessor(String event, Processor<T, R> processor);

    <T, R> SimpleEventBus addProcessorP(String event, ProcessorP<T, R> processorP);

    <T> SimpleEventBus addFilter(String event, Filter<T> handlerP);

    <T> SimpleEventBus addFilterP(String event, FilterP<T> handlerP);

    <T> SimpleEventBus addHandler(String event, EventHandler<T> handlerP);

    <T> SimpleEventBus addHandlerP(String event, EventHandlerP<T> handlerP);

    <T, R> SimpleEventBus addListener(String event, EventListener<T, R> eventListener);

    <R> Promise<R> fire(String event, Object t);

    <R> Promise<R> fire(String event, Object t, Map<String, ?> extra);
}
