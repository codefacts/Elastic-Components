package tracker.server.interceptors;

import io.vertx.ext.web.RoutingContext;
import tracker.server.request.handlers.RequestHandler;

/**
 * Created by sohan on 7/3/2017.
 */
public interface AuthInterceptor extends RequestHandler {
    @Override
    void handle(RoutingContext ctx);
}
