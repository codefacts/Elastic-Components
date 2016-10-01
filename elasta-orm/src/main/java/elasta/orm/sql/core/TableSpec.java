package elasta.orm.sql.core;

import java.util.List;

/**
 * Created by Jango on 9/15/2016.
 */
public class TableSpec {
    private final String tableName;
    private final String tableAlias;
    private final List<ColumnSpec> columnSpecs;

    public TableSpec(String tableName, String tableAlias, List<ColumnSpec> columnSpecs) {
        this.tableName = tableName;
        this.tableAlias = tableAlias;
        this.columnSpecs = columnSpecs;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public List<ColumnSpec> getColumnSpecs() {
        return columnSpecs;
    }
}
