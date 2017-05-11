package elasta.eventbus;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventHandlersPipeBuilder {

    <T, R> EventHandlersPipeBuilder addProcessor(Processor<T, R> processor);

    <T, R> EventHandlersPipeBuilder addProcessorP(ProcessorP<T, R> processorP);

    <T> EventHandlersPipeBuilder addFilter(Filter<T> filter);

    <T> EventHandlersPipeBuilder addFilterP(FilterP<T> filterP);

    <T> EventHandlersPipeBuilder addHandler(EventHandler<T> handler);

    <T> EventHandlersPipeBuilder addHandlerP(EventHandlerP<T> handlerP);

    List<ProcessorP> buildProcessorPList();

    EventHandlersPipe build();
}
