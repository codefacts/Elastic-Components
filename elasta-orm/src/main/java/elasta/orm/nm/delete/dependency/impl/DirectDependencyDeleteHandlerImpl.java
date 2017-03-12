package elasta.orm.nm.delete.dependency.impl;

import elasta.commons.Utils;
import elasta.orm.nm.delete.ColumnValuePair;
import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.dependency.DeleteFunction;
import elasta.orm.nm.delete.dependency.DirectDependencyDeleteHandler;
import elasta.orm.nm.delete.dependency.TableToTableDataMap;
import elasta.orm.nm.delete.dependency.ex.DirectDependencyDeleteHandlerException;
import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.TableData;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DirectDependencyDeleteHandlerImpl implements DirectDependencyDeleteHandler {
    final String dependentTable;
    final ColumnToColumnMapping[] columnToColumnMappings;
    final DeleteFunction dependentTableDeleteFunction;

    public DirectDependencyDeleteHandlerImpl(String dependentTable, ColumnToColumnMapping[] columnToColumnMappings, DeleteFunction dependentTableDeleteFunction) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(columnToColumnMappings);
        Objects.requireNonNull(dependentTableDeleteFunction);
        this.dependentTable = dependentTable;
        this.columnToColumnMappings = columnToColumnMappings;
        this.dependentTableDeleteFunction = dependentTableDeleteFunction;
    }

    @Override
    public void delete(TableData parentTableData, DeleteContext context, TableToTableDataMap tableToTableDataMap) {
        Collection<TableData> tableDatas = tableToTableDataMap.getAsCollection(dependentTable);
        Objects.requireNonNull(tableDatas);

        final ColumnValuePair[] columnValuePairs = new ColumnValuePair[columnToColumnMappings.length];

        for (int i = 0; i < columnToColumnMappings.length; i++) {
            ColumnToColumnMapping columnToColumnMapping = columnToColumnMappings[i];
            columnValuePairs[i] = new ColumnValuePair(
                columnToColumnMapping.getSrcColumn(),
                parentTableData.getValues().getValue(columnToColumnMapping.getDstColumn())
            );
        }

        final TableData tableData = tableDatas.stream()
            .filter(td -> {
                for (ColumnValuePair columnValuePair : columnValuePairs) {
                    boolean equals = td.getValues()
                        .getValue(columnValuePair.getPrimaryColumn())
                        .equals(columnValuePair.getValue());
                    if (Utils.not(equals)) {
                        return false;
                    }
                }
                return true;
            })
            .findAny()
            .orElseThrow(() -> new DirectDependencyDeleteHandlerException("No dependency data found for " + this.toString() + ""));

        dependentTableDeleteFunction.delete(tableData, context, tableToTableDataMap);
    }

    @Override
    public String toString() {
        return "DirectDependencyDeleteHandlerImpl{" +
            "dependentTable='" + dependentTable + '\'' +
            ", columnToColumnMappings=" + columnToColumnMappings +
            '}';
    }
}
