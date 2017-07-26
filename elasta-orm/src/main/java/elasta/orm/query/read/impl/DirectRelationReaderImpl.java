package elasta.orm.query.read.impl;

import elasta.orm.query.read.DirectRelationReader;
import elasta.orm.query.read.ObjectReader;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 17/02/12.
 */
final public class DirectRelationReaderImpl implements DirectRelationReader {
    final ObjectReader objectReader;

    public DirectRelationReaderImpl(ObjectReader objectReader) {
        Objects.requireNonNull(objectReader);
        this.objectReader = objectReader;
    }

    @Override
    public JsonObject read(JsonArray data, List<JsonArray> dataList) {
        return objectReader.read(data, dataList);
    }
}
