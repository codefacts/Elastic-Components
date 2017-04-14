package elasta.orm.event.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.event.EventDispatcher;
import elasta.orm.event.builder.BuilderContext;
import elasta.orm.event.builder.EntityToEventHandlerMap;
import elasta.orm.event.builder.EventDispatcherBuilder;
import elasta.orm.event.builder.ListChildsForEventHandlerFunction;
import elasta.orm.event.impl.ChildObjectEventHandler;
import elasta.orm.event.impl.EventDispatcherImpl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/28/2017.
 */
final public class EventDispatcherBuilderImpl implements EventDispatcherBuilder {
    final EntityToEventHandlerMap entityToEventHandlerMap;
    final ListChildsForEventHandlerFunction listChildsForEventHandlerFunction;

    public EventDispatcherBuilderImpl(EntityToEventHandlerMap entityToEventHandlerMap, ListChildsForEventHandlerFunction listChildsForEventHandlerFunction) {
        Objects.requireNonNull(entityToEventHandlerMap);
        Objects.requireNonNull(listChildsForEventHandlerFunction);
        this.entityToEventHandlerMap = entityToEventHandlerMap;
        this.listChildsForEventHandlerFunction = listChildsForEventHandlerFunction;
    }

    @Override
    public EventDispatcher build(String entity, BuilderContext<EventDispatcher> context) {

        if (context.isEmpty(entity)) {
            return new EventDispatcherProxyImpl(entity, context);
        }

        if (context.contains(entity)) {
            return context.get(entity);
        }

        context.putEmpty(entity);

        EventDispatcher eventDispatcher = buildEventDispatcher(entity, context);

        context.put(entity, eventDispatcher);

        return eventDispatcher;
    }

    private EventDispatcher buildEventDispatcher(String entity, BuilderContext<EventDispatcher> context) {
        return new EventDispatcherImpl(
            entityToEventHandlerMap.get(entity),
            childObjectEventHandlers(entity, context)
        );
    }

    private List<ChildObjectEventHandler> childObjectEventHandlers(String entity, BuilderContext<EventDispatcher> context) {

        ImmutableList.Builder<ChildObjectEventHandler> listBuilder = ImmutableList.builder();

        listChildsForEventHandlerFunction.listChildFields(entity).stream()
            .map(childField -> new ChildObjectEventHandler(
                childField.getField(),
                this.build(childField.getReferencingEntity(), context)
            )).forEach(listBuilder::add);

        return listBuilder.build();
    }
}
