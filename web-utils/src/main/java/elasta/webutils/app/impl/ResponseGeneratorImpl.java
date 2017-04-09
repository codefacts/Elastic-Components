package elasta.webutils.app.impl;

import elasta.webutils.app.ContentTypes;
import elasta.webutils.app.ResponseGenerator;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/7/2016.
 */
public class ResponseGeneratorImpl implements ResponseGenerator<JsonObject> {
    @Override
    public void reply(JsonObject jsonObject, RoutingContext context) {
        context.response().putHeader(HttpHeaders.CONTENT_TYPE, ContentTypes.APPLICATION_JSON);
        context.response().end(jsonObject.encode());
    }
}
