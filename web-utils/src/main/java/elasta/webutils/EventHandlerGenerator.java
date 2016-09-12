package elasta.webutils;

import com.google.common.collect.ImmutableList;
import elasta.commons.ReflectionUtils;
import elasta.module.ModuleSystem;
import io.vertx.core.eventbus.EventBus;

import java.util.List;

/**
 * Created by Jango on 9/12/2016.
 */
public class EventHandlerGenerator {
    final EventBus eventBus;
    final ModuleSystem moduleSystem;
    final EventNameGenerator eventNameGenerator;

    public EventHandlerGenerator(EventBus eventBus, ModuleSystem moduleSystem, EventNameGenerator eventNameGenerator) {
        this.eventBus = eventBus;
        this.moduleSystem = moduleSystem;
        this.eventNameGenerator = eventNameGenerator;
    }

    public List<EventSpec> handlerSpecs(String resourceName) {
        ImmutableList.Builder<EventSpec> builder = ImmutableList.builder();

        ReflectionUtils.staticFinalProps(EventHandlers.class).forEach(address -> {
            builder.add(new EventSpec(eventNameGenerator.eventName(address, resourceName), moduleSystem.require(EventHandler.class, address)));
        });

        return builder.build();
    }

    public void registerHandlers(List<EventSpec> eventSpecs) {
        eventSpecs.forEach(eventSpec -> eventBus.consumer(eventSpec.address, eventSpec.handler));
    }

    public static void main(String[] args) {
        System.out.println(ReflectionUtils.staticFinalProps(EventHandlers.class));
    }
}
