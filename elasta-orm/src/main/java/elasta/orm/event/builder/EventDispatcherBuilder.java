package elasta.orm.event.builder;

import elasta.orm.event.EventDispatcher;

/**
 * Created by sohan on 3/28/2017.
 */
public interface EventDispatcherBuilder {
    EventDispatcher build(String entity, BuilderContext<EventDispatcher> context);
}
