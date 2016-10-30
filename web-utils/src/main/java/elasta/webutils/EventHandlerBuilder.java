package elasta.webutils;

import elasta.core.flow.Flow;

/**
 * Created by Jango on 9/14/2016.
 */
public interface EventHandlerBuilder<T> {
    EventHandler<T> build(Flow machine);
}
