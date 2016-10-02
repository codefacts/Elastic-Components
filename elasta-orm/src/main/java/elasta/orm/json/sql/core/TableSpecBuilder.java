package elasta.orm.json.sql.core;

import java.util.List;

public class TableSpecBuilder {
    private String tableName;
    private String tableAlias;
    private List<ColumnSpec> columnSpecs;

    public TableSpecBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public TableSpecBuilder setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
        return this;
    }

    public TableSpecBuilder setColumnSpecs(List<ColumnSpec> columnSpecs) {
        this.columnSpecs = columnSpecs;
        return this;
    }

    public TableSpec createTableSpec() {
        return new TableSpec(tableName, tableAlias, columnSpecs);
    }
}