package elasta.orm.delete.impl;

import elasta.orm.delete.DeleteContext;
import elasta.orm.delete.IndirectDependencyDeleteHandler;
import elasta.sql.core.ColumnValuePair;
import elasta.sql.core.DeleteData;
import elasta.orm.upsert.ColumnToColumnMapping;
import elasta.orm.upsert.TableData;

import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class IndirectDependencyDeleteHandlerImpl implements IndirectDependencyDeleteHandler {
    final String relationTable;
    final ColumnToColumnMapping[] columnToColumnMappings;

    public IndirectDependencyDeleteHandlerImpl(String relationTable, ColumnToColumnMapping[] columnToColumnMappings) {
        Objects.requireNonNull(relationTable);
        Objects.requireNonNull(columnToColumnMappings);
        this.relationTable = relationTable;
        this.columnToColumnMappings = columnToColumnMappings;
    }

    @Override
    public void delete(TableData tableData, DeleteContext context) {
        context.add(
            new DeleteData(
                relationTable,
                columnValuePairs(tableData, columnToColumnMappings)
            )
        );
    }

    private ColumnValuePair[] columnValuePairs(TableData tableData, ColumnToColumnMapping[] columnToColumnMappings) {
        ColumnValuePair[] columnValuePairs = new ColumnValuePair[columnToColumnMappings.length];
        for (int i = 0; i < columnToColumnMappings.length; i++) {
            ColumnToColumnMapping columnToColumnMapping = columnToColumnMappings[i];
            columnValuePairs[i] = new ColumnValuePair(
                columnToColumnMapping.getDstColumn(),
                tableData.getValues().getValue(columnToColumnMapping.getSrcColumn())
            );
        }
        return columnValuePairs;
    }
}
