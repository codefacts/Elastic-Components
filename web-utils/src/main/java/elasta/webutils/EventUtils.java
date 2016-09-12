package elasta.webutils;

import com.google.common.collect.ImmutableList;
import elasta.commons.ReflectionUtils;
import elasta.module.ModuleSystem;
import io.vertx.core.eventbus.EventBus;

import java.util.List;

/**
 * Created by Jango on 9/12/2016.
 */
public class EventUtils {
    final EventBus eventBus;
    final ModuleSystem moduleSystem;

    public EventUtils(EventBus eventBus, ModuleSystem moduleSystem) {
        this.eventBus = eventBus;
        this.moduleSystem = moduleSystem;
    }

    public List<EventSpec> handlerSpecs(String resourceName) {
        ImmutableList.Builder<EventSpec> builder = ImmutableList.builder();

        ReflectionUtils.props(EventHandlers.class).forEach(address -> {
            builder.add(new EventSpec(address, moduleSystem.require(EventHandler.class, address)));
        });

        return builder.build();
    }

    public void registerHandlers(List<EventSpec> eventSpecs) {
        eventSpecs.forEach(eventSpec -> eventBus.consumer(eventSpec.address, eventSpec.handler));
    }

    public static void main(String[] args) {
        System.out.println(ReflectionUtils.props(EventHandlers.class));
    }
}
