package elasta.webutils.impl;

import elasta.webutils.ContentTypes;
import elasta.webutils.ResponseGenerator;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Created by sohan on 5/10/2017.
 */
final public class EmptyResponseGeneratorImpl implements ResponseGenerator<Object> {
    @Override
    public void reply(Message<Object> message, RoutingContext context) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(context);
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.TEXT_PLAIN);
        context.response().end();
    }
}
