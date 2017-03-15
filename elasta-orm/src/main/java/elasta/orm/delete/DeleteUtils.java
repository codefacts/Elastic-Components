package elasta.orm.delete;

import elasta.orm.upsert.TableData;
import elasta.sql.core.ColumnValuePair;

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
}
