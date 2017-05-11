package elasta.webutils.impl;

import com.google.common.collect.ImmutableMap;
import elasta.webutils.JsonArrayRequestConverter;
import elasta.webutils.exceptions.RequestConvertersionException;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 5/10/2017.
 */
final public class JsonArrayRequestConverterImpl implements JsonArrayRequestConverter {
    public JsonArrayRequestConverterImpl() {
    }

    @Override
    public JsonArray apply(RoutingContext context) throws Throwable {

        if (context.request().method() == HttpMethod.GET) {
            throw new RequestConvertersionException("Invalid HttpRequestMethod 'GET'");
        }

        checkContentType(context.request().headers().get(HttpHeaders.CONTENT_TYPE));

        final JsonArray jsonArray = context.getBodyAsJsonArray();

        checkJsonObject(jsonArray);

        return jsonArray;
    }

    private void checkJsonObject(JsonArray jsonArray) {
        if (jsonArray == null) {
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
