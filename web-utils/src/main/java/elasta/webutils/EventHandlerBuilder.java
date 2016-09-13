package elasta.webutils;

import elasta.core.statemachine.StateMachine;

/**
 * Created by Jango on 9/14/2016.
 */
public interface EventHandlerBuilder<T> {
    EventHandler<T> build(StateMachine machine);
}
