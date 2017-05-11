package elasta.eventbus.impl;

import com.google.common.collect.ImmutableList;
import elasta.eventbus.*;

import java.util.List;
import java.util.Objects;

import static elasta.eventbus.EventBusUtils.toProcessorP;

/**
 * Created by sohan on 5/9/2017.
 */
final public class EventHandlersPipeBuilderImpl implements EventHandlersPipeBuilder {
    final ImmutableList.Builder<ProcessorP> processorPListBuilder;

    public EventHandlersPipeBuilderImpl() {
        processorPListBuilder = ImmutableList.builder();
    }

    public EventHandlersPipeBuilderImpl(ImmutableList.Builder<ProcessorP> processorPListBuilder) {
        Objects.requireNonNull(processorPListBuilder);
        this.processorPListBuilder = processorPListBuilder;
    }

    @Override
    public <T, R> EventHandlersPipeBuilderImpl addProcessor(Processor<T, R> processor) {
        processorPListBuilder.add(toProcessorP(processor));
        return this;
    }

    @Override
    public <T, R> EventHandlersPipeBuilderImpl addProcessorP(ProcessorP<T, R> processorP) {
        processorPListBuilder.add(processorP);
        return this;
    }

    @Override
    public <T> EventHandlersPipeBuilderImpl addFilter(Filter<T> filter) {
        processorPListBuilder.add(toProcessorP(filter));
        return this;
    }

    @Override
    public <T> EventHandlersPipeBuilderImpl addFilterP(FilterP<T> filterP) {
        processorPListBuilder.add(toProcessorP(filterP));
        return this;
    }

    @Override
    public <T> EventHandlersPipeBuilderImpl addHandler(EventHandler<T> handler) {
        processorPListBuilder.add(toProcessorP(handler));
        return this;
    }

    @Override
    public <T> EventHandlersPipeBuilderImpl addHandlerP(EventHandlerP<T> handlerP) {
        processorPListBuilder.add(toProcessorP(handlerP));
        return this;
    }

    public List<ProcessorP> buildProcessorPList() {
        return processorPListBuilder.build();
    }

    @Override
    public EventHandlersPipe build() {
        return new EventHandlersPipeImpl(
            processorPListBuilder.build()
        );
    }
}
