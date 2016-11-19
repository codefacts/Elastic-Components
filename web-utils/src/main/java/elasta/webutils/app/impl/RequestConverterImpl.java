package elasta.webutils.app.impl;

import elasta.webutils.app.QueryStringToJsonObjectConverter;
import elasta.webutils.app.RequestConverter;
import elasta.webutils.app.RequestConvertersionException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 11/7/2016.
 */
public class RequestConverterImpl implements RequestConverter<JsonObject> {
    private final QueryStringToJsonObjectConverter queryStringToJsonObjectConverter;

    public RequestConverterImpl(QueryStringToJsonObjectConverter queryStringToJsonObjectConverter) {
        this.queryStringToJsonObjectConverter = queryStringToJsonObjectConverter;
    }

    @Override
    public JsonObject apply(RoutingContext context) throws Throwable {
        if (context.request().method() != HttpMethod.GET) {
            String contentType = context.request().headers().get(HttpHeaders.CONTENT_TYPE);
            if (contentType == null || contentType.isEmpty()) {
                throw new RequestConvertersionException("Content-Type is not present. Content-Type: '" + contentType + "'");
            }

            if (!contentType.trim().startsWith("application/json")) {
                throw new RequestConvertersionException("Unsupported content-type. Content-Type: '" + contentType + "'");
            }
        }
        JsonObject json = context.getBodyAsJson();
        json = json == null ? new JsonObject(new HashMap<>()) : json;
        final JsonObject jj = json;

        context.pathParams().forEach((pathVariable, value) -> {
            if (not(jj.containsKey(pathVariable))) {
                jj.put(pathVariable, value);
            }
            context.request().params().remove(pathVariable);
        });

        JsonObject params = queryStringToJsonObjectConverter.convert(context.request().params());

        return json.put("$params", params);
    }
}
