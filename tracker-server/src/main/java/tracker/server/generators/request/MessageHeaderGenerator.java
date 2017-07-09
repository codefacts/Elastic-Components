package tracker.server.generators.request;

import io.vertx.core.MultiMap;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by sohan on 7/8/2017.
 */
public interface MessageHeaderGenerator {
    MultiMap generate(RoutingContext context);
}
