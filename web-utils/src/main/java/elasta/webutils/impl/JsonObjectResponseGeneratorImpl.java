package elasta.webutils.impl;

import elasta.webutils.ContentTypes;
import elasta.webutils.JsonObjectResponseGenerator;
import elasta.webutils.ResponseGenerator;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Created by Jango on 11/7/2016.
 */
final public class JsonObjectResponseGeneratorImpl implements JsonObjectResponseGenerator {
    @Override
    public void reply(Message<JsonObject> message, RoutingContext context) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(context);
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
        context.response().end(message.body().encode());
    }
}
