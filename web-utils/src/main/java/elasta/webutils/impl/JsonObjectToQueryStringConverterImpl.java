package elasta.webutils.impl;

import elasta.webutils.JsonObjectToQueryStringConverter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 2016-11-20.
 */
public class JsonObjectToQueryStringConverterImpl<T> implements JsonObjectToQueryStringConverter {
    @Override
    public String convert(JsonObject jsonObject) {

        if (jsonObject == null) {
            return "";
        }

        StringBuilder stringBuilder = new StringBuilder();
        Serializer serializer = new Serializer(stringBuilder);
        serializer.seializeJsonObject(jsonObject);

        return stringBuilder.toString();
    }

    private final class Serializer {

        final StringBuilder stringBuilder;

        private Serializer(StringBuilder stringBuilder) {
            this.stringBuilder = stringBuilder;
        }

        private void seializeJsonObject(JsonObject jsonObject) {

            jsonObject.getMap().forEach((key, value) -> {

                if (value instanceof Map) {


                } else if (value instanceof JsonObject) {

                } else if (value instanceof List) {

                } else if (value instanceof JsonArray) {

                } else {

                }

            });

        }

        String serializedValue() {
            return stringBuilder.toString();
        }
    }
}
