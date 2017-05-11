package elasta.webutils.impl;

import com.google.common.collect.ImmutableMap;
import elasta.webutils.JsonObjectRequestConverter;
import elasta.webutils.query.string.QueryStringToJsonObjectConverter;
import elasta.webutils.RequestCnsts;
import elasta.webutils.exceptions.RequestConvertersionException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.HashMap;
import java.util.Map;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 11/7/2016.
 */
final public class JsonObjectRequestConverterImpl implements JsonObjectRequestConverter {

    public JsonObjectRequestConverterImpl() {
    }

    @Override
    public JsonObject apply(RoutingContext context) throws Throwable {

        if (context.request().method() == HttpMethod.GET) {
            throw new RequestConvertersionException("Invalid HttpRequestMethod 'GET'");
        }

        checkContentType(context.request().headers().get(HttpHeaders.CONTENT_TYPE));

        final JsonObject jsonObject = context.getBodyAsJson();

        checkJsonObject(jsonObject);

        return new JsonObject(
            ImmutableMap.<String, Object>builder()
                .putAll(jsonObject.getMap())
                .putAll(pathParams(context.pathParams(), jsonObject))
                .build()
        );
    }

    private Map<String, Object> pathParams(Map<String, String> pathParams, JsonObject jsonObject) {

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        pathParams.forEach((pathVariable, value) -> {

            if (jsonObject.containsKey(pathVariable)) {
                return;
            }

            builder.put(pathVariable, value);
        });

        return builder.build();
    }

    private void checkJsonObject(JsonObject jsonObject) {
        if (jsonObject == null) {
            throw new RequestConvertersionException("Request body must not be empty");
        }
    }

    private void checkContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            throw new RequestConvertersionException("Content-Type is not present. Content-Type: '" + contentType + "'");
        }

        if (not(contentType.trim().startsWith("application/json"))) {
            throw new RequestConvertersionException("Unsupported content-type. Content-Type: '" + contentType + "'");
        }
    }
}
