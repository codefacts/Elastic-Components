package elasta.orm.relation.delete.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.relation.delete.DeleteChildRelationsContext;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.RelationFieldHandler;
import elasta.sql.core.ColumnValuePair;
import elasta.sql.core.DeleteRelationData;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
final public class DirectChildRelationFieldHandlerImpl implements RelationFieldHandler {
    final String field;
    final String table;
    final List<ColumnToFieldMapping> primaryColumnToFieldMappings;
    final List<String> relationColumns;
    final DeleteChildRelationsFunction deleteChildRelationsFunction;

    public DirectChildRelationFieldHandlerImpl(String field, String table, List<ColumnToFieldMapping> primaryColumnToFieldMappings, List<String> relationColumns, DeleteChildRelationsFunction deleteChildRelationsFunction) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumnToFieldMappings);
        Objects.requireNonNull(relationColumns);
        Objects.requireNonNull(deleteChildRelationsFunction);
        this.field = field;
        this.table = table;
        this.primaryColumnToFieldMappings = primaryColumnToFieldMappings;
        this.relationColumns = relationColumns;
        this.deleteChildRelationsFunction = deleteChildRelationsFunction;
    }

    @Override
    public void handleDeleteRelation(JsonObject parent, DeleteChildRelationsContext context) {
        JsonObject jsonObject = parent.getJsonObject(field);

        if (jsonObject == null) {
            return;
        }

        deleteChildRelationsFunction.deleteChildRelations(
            jsonObject, context
        );

        addToContext(parent, context);
    }

    private void addToContext(JsonObject parent, DeleteChildRelationsContext context) {
        context.add(
            new DeleteRelationData(
                DeleteRelationData.OperationType.UPDATE,
                table,
                primaryColumnValuePairs(parent),
                relationColumns
            )
        );
    }

    private List<ColumnValuePair> primaryColumnValuePairs(JsonObject parent) {
        ImmutableList.Builder<ColumnValuePair> listBuilder = ImmutableList.builder();

        primaryColumnToFieldMappings.stream()
            .map(columnToFieldMapping -> new ColumnValuePair(
                columnToFieldMapping.getColumn(),
                parent.getValue(columnToFieldMapping.getField())
            ))
            .forEach(listBuilder::add);

        return listBuilder.build();
    }
}
