package tracker.server.generators.response.impl;

import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import tracker.server.ServerUtils;
import tracker.server.generators.response.FindOneHttpResponseGenerator;

/**
 * Created by sohan on 7/4/2017.
 */
final public class FindOneHttpResponseGeneratorImpl implements FindOneHttpResponseGenerator {
    @Override
    public HttpResponse generate(JsonObject value) {
        return new HttpResponse(
            HttpResponseStatus.OK.code(),
            ImmutableMap.of(
                HttpHeaderNames.CONTENT_TYPE.toString(), ServerUtils.APPLICATION_JSON_UTF_8
            ),
            value.encode()
        );
    }
}
