package elasta.composer.converter;

import elasta.composer.model.PageRequest;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/13/2017.
 */
@FunctionalInterface
public interface JsonObjectToPageRequestConverter extends Converter<JsonObject, PageRequest> {
    @Override
    PageRequest convert(JsonObject jsonObject);
}
