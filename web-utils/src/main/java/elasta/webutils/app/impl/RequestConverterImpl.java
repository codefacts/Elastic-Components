package elasta.webutils.app.impl;

import elasta.commons.Utils;
import elasta.webutils.app.RequestConverter;
import elasta.webutils.app.RequestConverterException;
import elasta.webutils.app.exceptions.WebException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Collections;
import java.util.HashMap;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 11/7/2016.
 */
public class RequestConverterImpl implements RequestConverter<JsonObject> {
    @Override
    public JsonObject apply(RoutingContext context) throws Throwable {
        if (context.request().method() != HttpMethod.GET) {
            String contentType = context.request().headers().get(HttpHeaders.CONTENT_TYPE);
            if (contentType == null || contentType.isEmpty()) {
                throw new RequestConverterException("Content-Type is not present. Content-Type: '" + contentType + "'");
            }

            if (!contentType.trim().startsWith("application/json")) {
                throw new RequestConverterException("Unsupported content-type. Content-Type: '" + contentType + "'");
            }
        }
        JsonObject json = context.getBodyAsJson();
        json = json == null ? new JsonObject(new HashMap<>()) : json;
        final JsonObject jj = json;
        context.request().params().forEach(e -> {
            if (not(jj.containsKey(e.getKey()))) {
                jj.put(e.getKey(), e.getValue());
            }
        });

        return json;
    }
}
