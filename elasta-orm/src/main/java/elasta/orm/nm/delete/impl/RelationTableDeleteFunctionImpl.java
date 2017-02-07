package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.*;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/07.
 */
final public class RelationTableDeleteFunctionImpl implements RelationTableDeleteFunction {
    final String relationTable;
    final FieldToRelationTableColumnMapping[] srcFieldToRelationTableColumnMappings;
    final FieldToRelationTableColumnMapping[] dstFieldToRelationTableColumnMappings;

    public RelationTableDeleteFunctionImpl(String relationTable, FieldToRelationTableColumnMapping[] srcFieldToRelationTableColumnMappings, FieldToRelationTableColumnMapping[] dstFieldToRelationTableColumnMappings) {
        Objects.requireNonNull(relationTable);
        Objects.requireNonNull(srcFieldToRelationTableColumnMappings);
        Objects.requireNonNull(dstFieldToRelationTableColumnMappings);
        this.relationTable = relationTable;
        this.srcFieldToRelationTableColumnMappings = srcFieldToRelationTableColumnMappings;
        this.dstFieldToRelationTableColumnMappings = dstFieldToRelationTableColumnMappings;
    }

    @Override
    public void delete(JsonObject parent, JsonObject jsonObject, DeleteContext deleteContext) {
        List<PrimaryColumnValuePair> pairs = Arrays.asList(srcFieldToRelationTableColumnMappings).stream()
            .map(mapping -> new PrimaryColumnValuePair(mapping.getRelationColumn(), parent.getValue(mapping.getSrcField())))
            .collect(Collectors.toList());

        List<PrimaryColumnValuePair> pairs1 = Arrays.asList(dstFieldToRelationTableColumnMappings).stream()
            .map(mapping -> new PrimaryColumnValuePair(mapping.getRelationColumn(), jsonObject.getValue(mapping.getSrcField())))
            .collect(Collectors.toList());

        pairs.addAll(pairs1);

        deleteContext.add(
            new DeleteData(relationTable, pairs.toArray(new PrimaryColumnValuePair[pairs.size()]))
        );
    }
}
