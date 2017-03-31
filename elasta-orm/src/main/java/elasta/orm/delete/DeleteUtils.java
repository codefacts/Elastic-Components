package elasta.orm.delete;

import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.upsert.TableData;
import elasta.sql.core.ColumnValuePair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteUtils {
    static ColumnValuePair[] columnValuePairs(TableData tableData) {
        final String[] primaryColumns = tableData.getPrimaryColumns();
        ColumnValuePair[] columnValuePairs = new ColumnValuePair[primaryColumns.length];
        for (int i = 0; i < primaryColumns.length; i++) {
            final String column = primaryColumns[i];
            columnValuePairs[i] = new ColumnValuePair(
                column,
                tableData.getValues().getValue(
                    column
                )
            );
        }
        return columnValuePairs;
    }

    static List<RelationMapping> getRelationMappingsForDelete(DbMapping dbMapping) {
        return Arrays.stream(dbMapping.getDbColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping instanceof RelationMapping).map(dbColumnMapping -> (RelationMapping) dbColumnMapping).collect(Collectors.toList());
    }
}
