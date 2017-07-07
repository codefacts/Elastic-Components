package tracker.server.generators.response;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/3/2017.
 */
public interface AddHttpResponseGenerator extends HttpResponseGenerator<JsonObject> {
    @Override
    HttpResponse generate(JsonObject value);
}
