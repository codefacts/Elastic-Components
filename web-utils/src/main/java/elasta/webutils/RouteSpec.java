package elasta.webutils;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 9/12/2016.
 */
public class RouteSpec {
    private final String uri;
    private final HttpMethod httpMethod;
    private final Handler<RoutingContext> handler;

    public RouteSpec(String uri, HttpMethod httpMethod, Handler<RoutingContext> handler) {
        this.uri = uri;
        this.httpMethod = httpMethod;
        this.handler = handler;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Handler<RoutingContext> getHandler() {
        return handler;
    }
}
