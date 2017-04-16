package elasta.orm.relation.delete.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.JavaType;
import elasta.orm.relation.delete.DeleteChildRelationsContext;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.RelationFieldHandler;
import elasta.sql.core.ColumnValuePair;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
final public class IndirectChildRelationFieldHandlerImpl implements RelationFieldHandler {
    final String field;
    final JavaType fieldJavaType;
    final String relationTable;
    final List<SrcFieldToRelationTableColumnMapping> srcSrcFieldToRelationTableColumnMappings;
    final List<SrcFieldToRelationTableColumnMapping> dstSrcFieldToRelationTableColumnMappings;
    final DeleteChildRelationsFunction deleteChildRelationsFunction;

    public IndirectChildRelationFieldHandlerImpl(String field, JavaType fieldJavaType, String relationTable, List<SrcFieldToRelationTableColumnMapping> srcSrcFieldToRelationTableColumnMappings, List<SrcFieldToRelationTableColumnMapping> dstSrcFieldToRelationTableColumnMappings, DeleteChildRelationsFunction deleteChildRelationsFunction) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(fieldJavaType);
        Objects.requireNonNull(relationTable);
        Objects.requireNonNull(srcSrcFieldToRelationTableColumnMappings);
        Objects.requireNonNull(dstSrcFieldToRelationTableColumnMappings);
        Objects.requireNonNull(deleteChildRelationsFunction);
        this.field = field;
        this.fieldJavaType = fieldJavaType;
        this.relationTable = relationTable;
        this.srcSrcFieldToRelationTableColumnMappings = srcSrcFieldToRelationTableColumnMappings;
        this.dstSrcFieldToRelationTableColumnMappings = dstSrcFieldToRelationTableColumnMappings;
        this.deleteChildRelationsFunction = deleteChildRelationsFunction;
    }

    @Override
    public void handleDeleteRelation(JsonObject parent, DeleteChildRelationsContext context) {
        switch (fieldJavaType) {
            case OBJECT: {
                JsonObject jsonObject = parent.getJsonObject(field);
                if (jsonObject == null) {
                    return;
                }

                deleteChildRelationsFunction.deleteChildRelations(jsonObject, context);

                handle(parent, jsonObject, context);
            }
            break;
            case ARRAY: {
                JsonArray jsonArray = parent.getJsonArray(field);

                if (jsonArray == null) {
                    return;
                }

                for (int i = 0; i < jsonArray.size(); i++) {

                    JsonObject jsonObject = jsonArray.getJsonObject(i);

                    Objects.requireNonNull(jsonObject);

                    deleteChildRelationsFunction.deleteChildRelations(jsonObject, context);

                    handle(parent, jsonObject, context);
                }
            }
            break;
        }
    }

    private void handle(JsonObject parent, JsonObject jsonObject, DeleteChildRelationsContext context) {
        context.add(
            new DeleteRelationData(
                DeleteRelationData.OperationType.DELETE,
                relationTable,
                primaryColumnValuePairs(parent, jsonObject)
            )
        );
    }

    private List<ColumnValuePair> primaryColumnValuePairs(JsonObject parent, JsonObject jsonObject) {

        ImmutableList.Builder<ColumnValuePair> listBuilder = ImmutableList.builder();

        srcSrcFieldToRelationTableColumnMappings.stream()
            .map(
                srcFieldToRelationTableColumnMapping -> new ColumnValuePair(
                    srcFieldToRelationTableColumnMapping.getDstColumn(),
                    parent.getValue(srcFieldToRelationTableColumnMapping.getSrcField())
                )
            )
            .forEach(listBuilder::add);

        dstSrcFieldToRelationTableColumnMappings.stream()
            .map(
                srcFieldToRelationTableColumnMapping -> new ColumnValuePair(
                    srcFieldToRelationTableColumnMapping.getDstColumn(),
                    jsonObject.getValue(srcFieldToRelationTableColumnMapping.getSrcField())
                )
            )
            .forEach(listBuilder::add);

        return listBuilder.build();
    }
}
