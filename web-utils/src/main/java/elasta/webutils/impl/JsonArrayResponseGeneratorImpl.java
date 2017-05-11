package elasta.webutils.impl;

import elasta.webutils.ContentTypes;
import elasta.webutils.JsonArrayResponseGenerator;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Objects;

/**
 * Created by sohan on 5/10/2017.
 */
final public class JsonArrayResponseGeneratorImpl implements JsonArrayResponseGenerator {
    @Override
    public void reply(Message<JsonArray> message, RoutingContext context) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(context);
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
        context.response().end(message.body().encode());
    }
}
