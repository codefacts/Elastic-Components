package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.DeleteData;
import elasta.orm.nm.delete.PrimaryColumnValuePair;
import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.TableData;

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
        
        final PrimaryColumnValuePair[] pairs = new PrimaryColumnValuePair[columnToColumnMappings.length];

        for (int i = 0; i < pairs.length; i++) {
            final ColumnToColumnMapping columnToColumnMapping = columnToColumnMappings[i];
            pairs[i] = new PrimaryColumnValuePair(
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
