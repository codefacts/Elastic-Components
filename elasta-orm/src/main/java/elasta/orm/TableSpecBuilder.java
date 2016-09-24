package elasta.orm;

import java.util.List;

public class TableSpecBuilder {
    private String tableName;
    private String primaryKey;
    private List<ColumnSpec> columnSpecs;
    private String tableAlias;

    public TableSpecBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public TableSpecBuilder setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public TableSpecBuilder setColumnSpecs(List<ColumnSpec> columnSpecs) {
        this.columnSpecs = columnSpecs;
        return this;
    }

    public TableSpec createTableSpec() {
        return new TableSpec(tableName, tableAlias, primaryKey, columnSpecs);
    }

    public TableSpecBuilder setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }
}