package elasta.webutils.app.impl;

import elasta.webutils.app.ResponseGenerator;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/7/2016.
 */
public class ResponseGeneratorImpl implements ResponseGenerator<JsonObject> {
    @Override
    public void reply(JsonObject t, RoutingContext context) {
        context.response().end(t.encode());
    }
}
