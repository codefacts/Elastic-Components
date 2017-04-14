package elasta.orm.event.builder.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.event.*;
import elasta.orm.event.builder.*;
import elasta.orm.event.impl.EntityToEventDispatcherMapImpl;
import elasta.orm.event.impl.EventProcessorImpl;

import java.util.*;

/**
 * Created by sohan on 3/30/2017.
 */
final public class EventProcessorBuilderImpl implements EventProcessorBuilder {
    final EntityMappingHelper helper;
    final Map<String, List<EventHandler>> deleteEventHandlersMap;
    final Map<String, List<EventHandler>> upsertEventHandlersMap;
    final Map<String, List<EventHandler>> deleteRelationEventHandlersMap;

    public EventProcessorBuilderImpl(EntityMappingHelper helper) {
        this(helper, new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public EventProcessorBuilderImpl(EntityMappingHelper helper, Map<String, List<EventHandler>> deleteEventHandlersMap, Map<String, List<EventHandler>> upsertEventHandlersMap, Map<String, List<EventHandler>> deleteRelationEventHandlersMap) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(deleteEventHandlersMap);
        Objects.requireNonNull(upsertEventHandlersMap);
        Objects.requireNonNull(deleteRelationEventHandlersMap);
        this.helper = helper;
        this.deleteEventHandlersMap = deleteEventHandlersMap;
        this.upsertEventHandlersMap = upsertEventHandlersMap;
        this.deleteRelationEventHandlersMap = deleteRelationEventHandlersMap;
    }

    @Override
    public EventProcessorBuilder onDelete(String entity, EventHandler eventHandler) {
        addDeleteEventHandler(entity, eventHandler);
        return this;
    }

    @Override
    public OnDeleteHandlersBuilder onDelete(String entity) {
        return new OnDeleteHandlersBuilder() {
            @Override
            public OnDeleteHandlersBuilder add(EventHandler eventHandler) {
                addDeleteEventHandler(entity, eventHandler);
                return this;
            }

            @Override
            public OnDeleteHandlersBuilder addAll(Collection<EventHandler> eventHandlers) {
                eventHandlers.forEach(eventHandler -> addDeleteEventHandler(entity, eventHandler));
                return this;
            }
        };
    }

    @Override
    public EventProcessorBuilder onUpsert(String entity, EventHandler eventHandler) {
        addUpsertEventHandler(entity, eventHandler);
        return this;
    }

    @Override
    public OnUpsertHandlersBuilder onUpsert(String entity) {
        return new OnUpsertHandlersBuilder() {
            @Override
            public OnUpsertHandlersBuilder add(EventHandler eventHandler) {
                addUpsertEventHandler(entity, eventHandler);
                return this;
            }

            @Override
            public OnUpsertHandlersBuilder addAll(Collection<EventHandler> eventHandlers) {
                eventHandlers.forEach(eventHandler -> addUpsertEventHandler(entity, eventHandler));
                return this;
            }
        };
    }

    @Override
    public EventProcessorBuilder onDeleteRelation(String entity, EventHandler eventHandler) {
        addDeleteRelationEventHandler(entity, eventHandler);
        return this;
    }

    @Override
    public OnDeleteRelationHandlersBuilder onDeleteRelation(String entity) {
        return new OnDeleteRelationHandlersBuilder() {
            @Override
            public OnDeleteRelationHandlersBuilder add(EventHandler eventHandler) {
                addDeleteRelationEventHandler(entity, eventHandler);
                return this;
            }

            @Override
            public OnDeleteRelationHandlersBuilder addAll(Collection<EventHandler> eventHandlers) {
                eventHandlers.forEach(eventHandler -> addDeleteRelationEventHandler(entity, eventHandler));
                return this;
            }
        };
    }

    @Override
    public EventProcessorBuilder on(String entity, OperationType operationType, EventHandler eventHandler) {
        switch (operationType) {
            case DELETE: {
                addDeleteEventHandler(entity, eventHandler);
            }
            break;
            case UPSERT: {
                addUpsertEventHandler(entity, eventHandler);
            }
            break;
            case DELETE_RELATION: {
                addDeleteRelationEventHandler(entity, eventHandler);
            }
            break;
        }
        return this;
    }

    private void addDeleteRelationEventHandler(String entity, EventHandler eventHandler) {
        List<EventHandler> eventHandlers = deleteRelationEventHandlersMap.get(entity);
        if (eventHandlers == null) {
            deleteRelationEventHandlersMap.put(entity, eventHandlers = new ArrayList<>());
        }
        eventHandlers.add(eventHandler);
    }

    @Override
    public EventProcessor build() {
        return new EventProcessorImpl(
            entityToEventDispatcherMap()
        );
    }

    private EntityToEventDispatcherMap entityToEventDispatcherMap() {
        ImmutableMap.Builder<String, EntityToEventDispatcherMapImpl.EventHandlerTpl> mapBuilder = ImmutableMap.builder();

        ImmutableSet<String> keySet = ImmutableSet.<String>builder()
            .addAll(deleteEventHandlersMap.keySet())
            .addAll(upsertEventHandlersMap.keySet())
            .addAll(deleteRelationEventHandlersMap.keySet())
            .build();

        BuilderContextImpl<EventDispatcher> builderContextDelete = new BuilderContextImpl<>(new LinkedHashMap<>());
        BuilderContextImpl<EventDispatcher> builderContextUpsert = new BuilderContextImpl<>(new LinkedHashMap<>());
        BuilderContextImpl<EventDispatcher> builderContextDeleteRelation = new BuilderContextImpl<>(new LinkedHashMap<>());

        EventDispatcherBuilderImpl deleteEventDispatcherBuilder = new EventDispatcherBuilderImpl(
            new EntityToEventHandlerMapImpl(
                deleteEventHandlersMap
            ),
            new ListChildsForDeleteEventHandlerFunctionImpl(helper)
        );

        EventDispatcherBuilderImpl upsertEventDispatcherBuilder = new EventDispatcherBuilderImpl(
            new EntityToEventHandlerMapImpl(
                upsertEventHandlersMap
            ),
            new ListChildsForUpsertEventHandlerFunctionImpl(helper)
        );

        EventDispatcherBuilderImpl deleteRelationEventDispatcherBuilder = new EventDispatcherBuilderImpl(
            new EntityToEventHandlerMapImpl(
                deleteRelationEventHandlersMap
            ),
            new ListChildsForDeleteRelationEventHandlerFunctionImpl(helper)
        );

        keySet.forEach(entity -> {

            mapBuilder.put(
                entity,
                new EntityToEventDispatcherMapImpl.EventHandlerTpl(
                    deleteEventDispatcherBuilder.build(
                        entity,
                        builderContextDelete
                    ),
                    upsertEventDispatcherBuilder.build(
                        entity,
                        builderContextUpsert
                    ),
                    deleteRelationEventDispatcherBuilder.build(
                        entity,
                        builderContextDeleteRelation
                    )
                )
            );
        });

        return new EntityToEventDispatcherMapImpl(
            mapBuilder.build()
        );
    }

    private void addDeleteEventHandler(String entity, EventHandler eventHandler) {
        List<EventHandler> eventHandlers = deleteEventHandlersMap.get(entity);
        if (eventHandlers == null) {
            deleteEventHandlersMap.put(entity, eventHandlers = new ArrayList<>());
        }
        eventHandlers.add(eventHandler);
    }

    private void addUpsertEventHandler(String entity, EventHandler eventHandler) {
        List<EventHandler> eventHandlers = upsertEventHandlersMap.get(entity);
        if (eventHandlers == null) {
            upsertEventHandlersMap.put(entity, eventHandlers = new ArrayList<>());
        }
        eventHandlers.add(eventHandler);
    }

    public static void main(String[] asdf) {

    }
}
