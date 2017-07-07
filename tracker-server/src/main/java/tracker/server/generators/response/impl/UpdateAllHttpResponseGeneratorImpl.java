package tracker.server.generators.response.impl;

import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import tracker.server.ServerUtils;
import tracker.server.generators.response.UpdateAllHttpResponseGenerator;

/**
 * Created by sohan on 7/4/2017.
 */
final public class UpdateAllHttpResponseGeneratorImpl implements UpdateAllHttpResponseGenerator {
    @Override
    public HttpResponse generate(Object value) {
        return new HttpResponse(
            HttpResponseStatus.OK.code(),
            ImmutableMap.of(
                HttpHeaderNames.CONTENT_TYPE.toString(), ServerUtils.APPLICATION_JSON_UTF_8
            ),
            ""
        );
    }
}
