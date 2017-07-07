package tracker.server.generators.request;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by sohan on 7/4/2017.
 */
@FunctionalInterface
public interface MessageBodyGenerator<T> {
    T generate(RoutingContext ctx);
}
