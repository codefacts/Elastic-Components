package elasta.orm.nm.delete.impl;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
public class JsonDependencyHandler {
    final JoHandler joHandler;

    public JsonDependencyHandler(JoHandler joHandler) {
        Objects.requireNonNull(joHandler);
        this.joHandler = joHandler;
    }

    void handle(Object value) {

        if (value instanceof JsonObject) {

            handleJo((JsonObject) value);

        } else if (value instanceof Map) {

            handleJo(new JsonObject(toMap(value)));

        } else if (value instanceof JsonArray) {

            handleJa((JsonArray) value);

        } else if (value instanceof List) {

            handleJa(new JsonArray(toList(value)));
        }
    }

    private void handleJo(JsonObject value) {
        joHandler.handle(value);
    }

    private void handleJa(JsonArray value) {
        for (int i = 0; i < value.size(); i++) {
            handleJo(value.getJsonObject(i));
        }
    }

    private List toList(Object value) {
        return (List) value;
    }

    private Map<String, Object> toMap(Object value) {
        return (Map<String, Object>) value;
    }
}
