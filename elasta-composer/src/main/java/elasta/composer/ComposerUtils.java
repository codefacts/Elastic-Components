package elasta.composer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import elasta.composer.builder.impl.ConvertersMapBuilderImpl;
import elasta.composer.impl.ContextImpl;
import elasta.composer.impl.HeadersImpl;
import elasta.composer.impl.VertxMultiMap;
import elasta.composer.model.request.UserModel;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Created by sohan on 5/12/2017.
 */
public interface ComposerUtils {
    JsonObject JSON_OBJECT = new JsonObject(ImmutableMap.of());
    JsonArray JSON_ARRAY = new JsonArray(ImmutableList.of());

    static JsonObject emptyJsonObject() {
        return JSON_OBJECT;
    }

    static JsonArray emptyJsonArray() {
        return JSON_ARRAY;
    }

    static MultiMap toVertxMultimap(ListMultimap<String, String> headers) {
        return new VertxMultiMap(headers);
    }

    static <T> Msg<T> toMsg(Message<T> message, String userId) {
        return Msg.<T>builder()
            .body(message.body())
            .headers(
                new HeadersImpl(
                    ImmutableListMultimap.of(
                        UserModel.userId, userId
                    ),
                    new ConvertersMapBuilderImpl().build().getMap()
                )
            )
            .context(new ContextImpl(ImmutableMap.of()))
            .userId(userId)
            .build();
    }

    static ImmutableListMultimap<String, String> toListMultimap(MultiMap headers) {
        
        ImmutableListMultimap.Builder<String, String> builder = ImmutableListMultimap.builder();

        headers.forEach(entry -> builder.put(entry.getKey(), entry.getValue()));

        return builder.build();
    }
}
