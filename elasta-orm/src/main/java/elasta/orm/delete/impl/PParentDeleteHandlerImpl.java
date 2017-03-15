package elasta.orm.delete.impl;

import elasta.sql.core.ColumnValuePair;
import elasta.orm.delete.DeleteContext;
import elasta.sql.core.DeleteData;
import elasta.orm.upsert.ColumnToColumnMapping;
import elasta.orm.upsert.TableData;

import java.util.Objects;

/**
 * Created by Jango on 17/02/16.
 */
public class PParentDeleteHandlerImpl implements PParentDeleteHandler {
    final String parentTable;
    final ColumnToColumnMapping[] columnToColumnMappings;

    public PParentDeleteHandlerImpl(String parentTable, ColumnToColumnMapping[] columnToColumnMappings) {
        Objects.requireNonNull(parentTable);
        Objects.requireNonNull(columnToColumnMappings);
        this.parentTable = parentTable;
        this.columnToColumnMappings = columnToColumnMappings;
    }

    @Override
    public void delete(TableData childTableData, DeleteContext deleteContext) {
        
        final ColumnValuePair[] pairs = new ColumnValuePair[columnToColumnMappings.length];

        for (int i = 0; i < pairs.length; i++) {
            final ColumnToColumnMapping columnToColumnMapping = columnToColumnMappings[i];
            pairs[i] = new ColumnValuePair(
                columnToColumnMapping.getSrcColumn(),
                childTableData.getValues().getValue(columnToColumnMapping.getDstColumn())
            );
        }

        deleteContext.add(
            new DeleteData(
                parentTable,
                pairs
            )
        );
    }
}
