package tracker.server.generators.response;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/4/2017.
 */
public interface FindOneHttpResponseGenerator extends HttpResponseGenerator<JsonObject> {
    @Override
    HttpResponse generate(JsonObject value);
}
