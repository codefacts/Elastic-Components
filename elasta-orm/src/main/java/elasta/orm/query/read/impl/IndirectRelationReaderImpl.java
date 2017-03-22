package elasta.orm.query.read.impl;

import elasta.orm.query.read.ObjectReader;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/12.
 */
final public class IndirectRelationReaderImpl implements IndirectRelationReader {
    final String primaryField;
    final ObjectReader objectReader;

    public IndirectRelationReaderImpl(String primaryField, ObjectReader objectReader) {
        Objects.requireNonNull(primaryField);
        Objects.requireNonNull(objectReader);
        this.primaryField = primaryField;
        this.objectReader = objectReader;
    }

    @Override
    public List<JsonObject> read(JsonArray data, List<JsonArray> dataList) {

        final Map<Object, List<JsonObject>> listMap = dataList.stream().map(jsonArray -> objectReader.read(jsonArray, dataList))
            .collect(Collectors.groupingBy(jsonObject -> jsonObject.getValue(primaryField)));

        return listMap.entrySet().stream().map(entry -> entry.getValue().get(0)).collect(Collectors.toList());
    }
}
