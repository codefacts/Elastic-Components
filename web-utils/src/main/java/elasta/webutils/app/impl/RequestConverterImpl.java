package elasta.webutils.app.impl;

import elasta.webutils.app.RequestConverter;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Jango on 11/7/2016.
 */
public class RequestConverterImpl implements RequestConverter<JsonObject> {
    @Override
    public JsonObject apply(RoutingContext context) throws Throwable {
        JsonObject json = context.getBodyAsJson();
        return json == null ? new JsonObject(new HashMap<>()) : json;
    }
}
