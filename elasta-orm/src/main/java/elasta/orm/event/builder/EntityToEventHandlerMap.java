package elasta.orm.event.builder;

import elasta.orm.event.EventHandler;

/**
 * Created by sohan on 3/28/2017.
 */
public interface EntityToEventHandlerMap {
    EventHandler get(String entity);
}
