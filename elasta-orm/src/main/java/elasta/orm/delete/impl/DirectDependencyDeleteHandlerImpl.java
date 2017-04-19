package elasta.orm.delete.impl;

import elasta.commons.Utils;
import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.DirectDependencyDeleteHandler;
import elasta.orm.delete.TableToTableDataMap;
import elasta.orm.delete.DeleteContext;
import elasta.sql.core.ColumnToColumnMapping;
import elasta.orm.upsert.TableData;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DirectDependencyDeleteHandlerImpl implements DirectDependencyDeleteHandler {
    final String dependentTable;
    final ColumnToColumnMapping[] columnToColumnMappings;
    final DeleteTableFunction dependentTableDeleteTableFunction;

    public DirectDependencyDeleteHandlerImpl(String dependentTable, ColumnToColumnMapping[] columnToColumnMappings, DeleteTableFunction dependentTableDeleteTableFunction) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(columnToColumnMappings);
        Objects.requireNonNull(dependentTableDeleteTableFunction);
        this.dependentTable = dependentTable;
        this.columnToColumnMappings = columnToColumnMappings;
        this.dependentTableDeleteTableFunction = dependentTableDeleteTableFunction;
    }

    @Override
    public void delete(TableData parentTableData, DeleteContext context, TableToTableDataMap tableToTableDataMap) {
        Collection<TableData> tableDatas = tableToTableDataMap.getAsCollection(dependentTable);

        final ColumnValuePair[] columnValuePairs = new ColumnValuePair[columnToColumnMappings.length];

        for (int i = 0; i < columnToColumnMappings.length; i++) {
            ColumnToColumnMapping columnToColumnMapping = columnToColumnMappings[i];
            columnValuePairs[i] = new ColumnValuePair(
                columnToColumnMapping.getSrcColumn(),
                parentTableData.getValues().getValue(columnToColumnMapping.getDstColumn())
            );
        }

        tableDatas.stream()
            .filter(td -> {
                for (ColumnValuePair columnValuePair : columnValuePairs) {
                    boolean equals = columnValuePair.getValue().equals(
                        td.getValues()
                            .getValue(columnValuePair.getPrimaryColumn())
                    );
                    if (Utils.not(equals)) {
                        return false;
                    }
                }
                return true;
            })
            .forEach(tableData -> dependentTableDeleteTableFunction.delete(tableData, context, tableToTableDataMap));
        ;
    }

    @Override
    public String toString() {
        return "DirectDependencyDeleteHandlerImpl{" +
            "parentTable='" + dependentTable + '\'' +
            ", columnToColumnMappings=" + Arrays.toString(columnToColumnMappings) +
            ", dependentTableDeleteTableFunction=" + dependentTableDeleteTableFunction +
            '}';
    }
}
