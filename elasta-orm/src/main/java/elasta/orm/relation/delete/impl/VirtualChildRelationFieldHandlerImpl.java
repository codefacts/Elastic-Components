package elasta.orm.relation.delete.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.JavaType;
import elasta.orm.relation.delete.DeleteChildRelationsContext;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.RelationFieldHandler;
import elasta.orm.relation.delete.ex.VirtualChildRelationFieldHandlerException;
import elasta.orm.delete.impl.ColumnValuePair;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
final public class VirtualChildRelationFieldHandlerImpl implements RelationFieldHandler {
    final String field;
    final JavaType fieldJavaType;
    final String referencingTable;
    final List<ColumnToFieldMapping> referencingTablePrimaryColumnToFieldMappings;
    final List<String> referencingTableRelationColumns;
    final DeleteChildRelationsFunction deleteChildRelationsFunction;

    public VirtualChildRelationFieldHandlerImpl(String field, JavaType fieldJavaType, String referencingTable, List<ColumnToFieldMapping> referencingTablePrimaryColumnToFieldMappings, List<String> referencingTableRelationColumns, DeleteChildRelationsFunction deleteChildRelationsFunction) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(fieldJavaType);
        Objects.requireNonNull(referencingTable);
        Objects.requireNonNull(referencingTablePrimaryColumnToFieldMappings);
        Objects.requireNonNull(referencingTableRelationColumns);
        Objects.requireNonNull(deleteChildRelationsFunction);
        this.field = field;
        this.fieldJavaType = fieldJavaType;
        this.referencingTable = referencingTable;
        this.referencingTablePrimaryColumnToFieldMappings = referencingTablePrimaryColumnToFieldMappings;
        this.referencingTableRelationColumns = referencingTableRelationColumns;
        this.deleteChildRelationsFunction = deleteChildRelationsFunction;
    }

    @Override
    public void handleDeleteRelation(JsonObject parent, DeleteChildRelationsContext context) {
        switch (fieldJavaType) {
            case ARRAY: {
                JsonArray jsonArray = parent.getJsonArray(field);
                if (jsonArray == null) {
                    return;
                }

                for (int i = 0; i < jsonArray.size(); i++) {

                    JsonObject jsonObject = jsonArray.getJsonObject(i);

                    Objects.requireNonNull(jsonObject);

                    deleteChildRelationsFunction.deleteChildRelations(jsonObject, context);

                    handle(jsonObject, context);
                }
            }
            break;
            case OBJECT: {

                JsonObject jsonObject = parent.getJsonObject(field);

                if (jsonObject == null) {
                    return;
                }

                deleteChildRelationsFunction.deleteChildRelations(jsonObject, context);

                handle(jsonObject, context);
            }
            break;
            default: {
                throw new VirtualChildRelationFieldHandlerException("Invalid fieldJavaType '" + fieldJavaType + "' for column '" + field + "'");
            }
        }
    }

    private void handle(JsonObject jsonObject, DeleteChildRelationsContext context) {
        context.add(
            new DeleteRelationData(
                DeleteRelationData.OperationType.UPDATE,
                referencingTable,
                primaryColumnValuePairs(jsonObject),
                referencingTableRelationColumns
            )
        );
    }

    private List<ColumnValuePair> primaryColumnValuePairs(JsonObject jsonObject) {

        ImmutableList.Builder<ColumnValuePair> listBuilder = ImmutableList.builder();

        referencingTablePrimaryColumnToFieldMappings.stream()
            .map(columnToFieldMapping -> new ColumnValuePair(
                columnToFieldMapping.getColumn(),
                jsonObject.getValue(columnToFieldMapping.getField())
            ))
            .forEach(listBuilder::add);

        return listBuilder.build();
    }
}
