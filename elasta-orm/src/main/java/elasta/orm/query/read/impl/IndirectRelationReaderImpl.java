package elasta.orm.query.read.impl;

import elasta.orm.query.read.IndirectRelationReader;
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
    final int parentPrimaryKeyIndex;

    public IndirectRelationReaderImpl(String primaryField, ObjectReader objectReader, int parentPrimaryKeyIndex) {
        Objects.requireNonNull(primaryField);
        Objects.requireNonNull(objectReader);
        this.primaryField = primaryField;
        this.objectReader = objectReader;
        this.parentPrimaryKeyIndex = parentPrimaryKeyIndex;
    }

    @Override
    public List<JsonObject> read(Object parentId, JsonArray data, List<JsonArray> dataList) {

        final Map<Object, List<JsonObject>> listMap = dataList.stream()
            .filter(jsonArray -> Objects.equals(jsonArray.getValue(parentPrimaryKeyIndex), parentId))
            .map(jsonArray -> objectReader.read(jsonArray, dataList))
            .collect(Collectors.groupingBy(jsonObject -> jsonObject.getValue(primaryField)));

        return listMap.entrySet().stream().map(entry -> entry.getValue().get(0)).collect(Collectors.toList());
    }
}
