package elasta.orm.nm.read.impl;

import elasta.orm.nm.read.ObjectReader;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 17/02/12.
 */
final public class ObjectReaderImpl implements ObjectReader {
    final List<FieldAndIndexPair> fieldAndIndexPairs;
    final List<DirectRelation> directRelations;
    final List<IndirectRelation> indirectRelations;

    public ObjectReaderImpl(List<FieldAndIndexPair> fieldAndIndexPairs, List<DirectRelation> directRelations, List<IndirectRelation> indirectRelations) {
        Objects.requireNonNull(fieldAndIndexPairs);
        Objects.requireNonNull(directRelations);
        Objects.requireNonNull(indirectRelations);
        this.fieldAndIndexPairs = fieldAndIndexPairs;
        this.directRelations = directRelations;
        this.indirectRelations = indirectRelations;
    }

    @Override
    public JsonObject read(JsonArray data, List<JsonArray> dataList) {

        HashMap<String, Object> map = new HashMap<>();

        fieldAndIndexPairs.forEach(fieldAndIndexPair -> {
            map.put(fieldAndIndexPair.getField(), data.getValue(fieldAndIndexPair.getIndex()));
        });

        directRelations.forEach(directRelation -> {
            map.put(directRelation.getField(), directRelation.getDirectRelationReader().read(data, dataList));
        });

        indirectRelations.forEach(indirectRelation -> {
            map.put(indirectRelation.getField(), indirectRelation.getIndirectRelationReader().read(data, dataList));
        });

        return new JsonObject(map);
    }
}
