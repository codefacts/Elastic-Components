package tracker.server.generators.response;

import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 7/4/2017.
 */
public interface AddAllHttpResponseGenerator extends HttpResponseGenerator<JsonArray> {
    @Override
    HttpResponse generate(JsonArray value);
}
