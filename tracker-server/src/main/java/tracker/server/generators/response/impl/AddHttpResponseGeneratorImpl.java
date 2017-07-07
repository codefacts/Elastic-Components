package tracker.server.generators.response.impl;

import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import tracker.server.ServerUtils;
import tracker.server.generators.response.AddHttpResponseGenerator;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 7/4/2017.
 */
final public class AddHttpResponseGeneratorImpl implements AddHttpResponseGenerator {
    final Collection<String> fields;

    public AddHttpResponseGeneratorImpl(Collection<String> fields) {
        Objects.requireNonNull(fields);
        this.fields = fields;
    }

    @Override
    public HttpResponse generate(JsonObject value) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        fields.forEach(field -> {
            builder.put(field, value.getValue(field));
        });

        return new HttpResponse(
            HttpResponseStatus.CREATED.code(),
            ImmutableMap.of(
                HttpHeaderNames.CONTENT_TYPE.toString(), ServerUtils.APPLICATION_JSON_UTF_8
            ),
            new JsonObject(builder.build()).encode()
        );
    }
}
