package elasta.orm.delete.impl;

import elasta.orm.delete.DeleteContext;
import elasta.orm.delete.DirectRelationDeleteHandler;
import elasta.orm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/18/2017.
 */
final public class DirectRelationDeleteHandlerImpl implements DirectRelationDeleteHandler {
    final String parentTable;
    final DeleteData.ColumnAndOptionalValuePair[] columnAndOptionalValuePairs;
    final List<String> childTableColumns;

    public DirectRelationDeleteHandlerImpl(String parentTable, DeleteData.ColumnAndOptionalValuePair[] columnAndOptionalValuePairs, List<String> childTableColumns) {
        Objects.requireNonNull(parentTable);
        Objects.requireNonNull(columnAndOptionalValuePairs);
        Objects.requireNonNull(childTableColumns);
        this.parentTable = parentTable;
        this.columnAndOptionalValuePairs = columnAndOptionalValuePairs;
        this.childTableColumns = childTableColumns;
    }

    @Override
    public void deleteRelation(TableData tableData, DeleteContext context) {
        context.add(
            new DeleteData(
                DeleteData.OperationType.UPDATE,
                parentTable,
                columnAndOptionalValuePairs,
                whereCriteria(tableData)
            )
        );
    }

    private ColumnValuePair[] whereCriteria(TableData tableData) {

        ColumnValuePair[] pairs = new ColumnValuePair[childTableColumns.size()];

        JsonObject tableDataValues = tableData.getValues();

        for (int i = 0; i < childTableColumns.size(); i++) {

            final String column = childTableColumns.get(i);

            pairs[i] = new ColumnValuePair(
                column,
                tableDataValues.getValue(column)
            );
        }

        return pairs;
    }
}
