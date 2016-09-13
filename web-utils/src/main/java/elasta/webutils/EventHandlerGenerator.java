package elasta.webutils;

import com.google.common.collect.ImmutableList;
import elasta.commons.ReflectionUtils;
import elasta.core.statemachine.StateMachine;
import elasta.module.ModuleSystem;
import io.vertx.core.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/12/2016.
 */
public class EventHandlerGenerator {
    final EventBus eventBus;
    final ModuleSystem moduleSystem;
    final EventNameGenerator eventNameGenerator;
    final EventHandlerBuilder eventHandlerBuilder;

    public EventHandlerGenerator(EventBus eventBus, ModuleSystem moduleSystem, EventNameGenerator eventNameGenerator, EventHandlerBuilder eventHandlerBuilder) {
        this.eventBus = eventBus;
        this.moduleSystem = moduleSystem;
        this.eventNameGenerator = eventNameGenerator;
        this.eventHandlerBuilder = eventHandlerBuilder;
    }

    public List<EventSpec> makeHandlers(String resourceName, Map<String, StateMachine> machineMap) {
        ImmutableList.Builder<EventSpec> builder = ImmutableList.builder();

        ReflectionUtils.staticFinalValues(EventAddresses.class).forEach(
            address ->
                builder.add(
                    new EventSpec(
                        eventNameGenerator.eventName(address, resourceName),
                        eventHandlerBuilder.build(
                            machineMap.get(address)
                        )
                    )
                ));

        return builder.build();
    }

    public void registerHandlers(List<EventSpec> eventSpecs) {
        eventSpecs.forEach(eventSpec -> eventBus.consumer(eventSpec.address, eventSpec.handler));
    }

    public static void main(String[] args) {
        System.out.println(ReflectionUtils.staticFinalValues(EventAddresses.class));
    }
}
