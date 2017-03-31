package elasta.orm.event.impl;

import elasta.orm.event.EventDispatcher;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/28/2017.
 */
@Value
final public class ChildObjectEventHandler {
    final String field;
    final EventDispatcher eventDispatcher;

    public ChildObjectEventHandler(String field, EventDispatcher eventDispatcher) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(eventDispatcher);
        this.field = field;
        this.eventDispatcher = eventDispatcher;
    }
}
