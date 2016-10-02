package elasta.orm.json.sql.core;

public class ColumnSpecBuilder {
    private String columnName;
    private JoinSpec joinSpec;

    public ColumnSpecBuilder setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public ColumnSpecBuilder setJoinSpec(JoinSpec joinSpec) {
        this.joinSpec = joinSpec;
        return this;
    }

    public ColumnSpec createColumnSpec() {
        return new ColumnSpec(columnName, joinSpec);
    }
}