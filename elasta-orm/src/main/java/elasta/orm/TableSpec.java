package elasta.orm;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public class TableSpec {
    private final String tableName;
    private final String primaryKey;
    private final List<ColumnSpec> columnSpecs;

    public TableSpec(String tableName, String primaryKey, List<ColumnSpec> columnSpecs) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.columnSpecs = columnSpecs;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public List<ColumnSpec> getColumnSpecs() {
        return columnSpecs;
    }
}
