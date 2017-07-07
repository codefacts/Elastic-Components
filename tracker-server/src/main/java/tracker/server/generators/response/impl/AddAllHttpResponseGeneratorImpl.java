package tracker.server.generators.response.impl;

import com.google.common.collect.ImmutableMap;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import tracker.server.ServerUtils;
import tracker.server.generators.response.AddAllHttpResponseGenerator;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 7/4/2017.
 */
final public class AddAllHttpResponseGeneratorImpl implements AddAllHttpResponseGenerator {
    final List<String> fields;

    public AddAllHttpResponseGeneratorImpl(List<String> fields) {
        Objects.requireNonNull(fields);
        this.fields = fields;
    }

    @Override
    public HttpResponse generate(JsonArray value) {

        List<JsonObject> list = value.getList();

        List<JsonObject> jsonObjects = list.stream().map(this::selectFields).collect(Collectors.toList());

        return new HttpResponse(
            HttpResponseStatus.CREATED.code(),
            ImmutableMap.of(
                HttpHeaderNames.CONTENT_TYPE.toString(), ServerUtils.APPLICATION_JSON_UTF_8
            ),
            new JsonArray(jsonObjects).encode()
        );
    }

    private JsonObject selectFields(JsonObject jsonObject) {

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();

        fields.forEach(field -> builder.put(field, jsonObject.getValue(field)));

        return new JsonObject(builder.build());
    }
}
