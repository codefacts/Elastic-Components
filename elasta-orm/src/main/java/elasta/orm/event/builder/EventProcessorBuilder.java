package elasta.orm.event.builder;

import elasta.orm.event.EventHandler;
import elasta.orm.event.EventProcessor;
import elasta.orm.event.OperationType;

import java.util.Collection;

/**
 * Created by sohan on 3/30/2017.
 */
public interface EventProcessorBuilder {

    EventProcessorBuilder onDelete(String entity, EventHandler eventHandler);

    OnDeleteHandlersBuilder onDelete(String entity);

    EventProcessorBuilder onUpsert(String entity, EventHandler eventHandler);

    OnUpsertHandlersBuilder onUpsert(String entity);

    EventProcessorBuilder on(String entity, OperationType operationType, EventHandler eventHandler);

    EventProcessor build();

    interface OnDeleteHandlersBuilder {

        OnDeleteHandlersBuilder add(EventHandler eventHandler);

        OnDeleteHandlersBuilder addAll(Collection<EventHandler> eventHandlers);
    }

    interface OnUpsertHandlersBuilder {

        OnUpsertHandlersBuilder add(EventHandler eventHandler);

        OnUpsertHandlersBuilder addAll(Collection<EventHandler> eventHandlers);
    }
}
