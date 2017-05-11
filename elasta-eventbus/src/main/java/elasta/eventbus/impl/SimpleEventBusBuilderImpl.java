package elasta.eventbus.impl;

import elasta.eventbus.SimpleEventBus;
import elasta.eventbus.SimpleEventBusBuilder;

import java.util.Objects;

/**
 * Created by sohan on 5/9/2017.
 */
final public class SimpleEventBusBuilderImpl implements SimpleEventBusBuilder {
    final io.vertx.core.eventbus.EventBus eventBus;

    public SimpleEventBusBuilderImpl(io.vertx.core.eventbus.EventBus eventBus) {
        Objects.requireNonNull(eventBus);
        this.eventBus = eventBus;
    }

    @Override
    public SimpleEventBus build() {
        return new SimpleEventBusImpl(eventBus);
    }
}
