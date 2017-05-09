package elasta.webutils.impl;

import elasta.webutils.JsonObjectRequestConverter;
import elasta.webutils.QueryStringToJsonObjectConverter;
import elasta.webutils.RequestCnsts;
import elasta.webutils.exceptions.RequestConvertersionException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 11/7/2016.
 */
final public class JsonObjectRequestConverterImpl implements JsonObjectRequestConverter {
    private final QueryStringToJsonObjectConverter queryStringToJsonObjectConverter;

    public JsonObjectRequestConverterImpl(QueryStringToJsonObjectConverter queryStringToJsonObjectConverter) {
        this.queryStringToJsonObjectConverter = queryStringToJsonObjectConverter;
    }

    @Override
    public JsonObject apply(RoutingContext context) throws Throwable {
        if (context.request().method() != HttpMethod.GET) {
            String contentType = context.request().headers().get(HttpHeaders.CONTENT_TYPE);
            if (contentType == null || contentType.isEmpty()) {
                throw new RequestConvertersionException("Content-Type is not present. Content-Type: '" + contentType + "'");
            }

            if (not(contentType.trim().startsWith("application/json"))) {
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

        return json.put(
            RequestCnsts.META,
            new JsonObject()
                .put(RequestCnsts.PARAMS,
                    queryStringToJsonObjectConverter
                        .convert(context.request().params())
                ));
    }
}
