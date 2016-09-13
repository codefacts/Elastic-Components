package elasta.webutils;

import elasta.core.statemachine.StateMachine;
import io.vertx.ext.web.Router;

import java.util.Map;

/**
 * Created by Jango on 9/13/2016.
 */
@FunctionalInterface
public interface CrudBuilder {
    void addRoutesAndHandlers(Router router, String prefixUri, String resourceName, Map<String, StateMachine> machineMap);
}
