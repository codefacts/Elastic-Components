package elasta.composer.interceptor;

import elasta.composer.Msg;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/9/2017.
 */
public interface QueryMsgInterceptor {
    JsonObject intercept(Msg<JsonObject> query);
}
