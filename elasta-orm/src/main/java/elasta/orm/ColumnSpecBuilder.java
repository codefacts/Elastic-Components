package elasta.orm;

public class ColumnSpecBuilder {
    private String columnName;
    private JavaType columnType;
    private JoinSpec joinSpec;

    public ColumnSpecBuilder setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public ColumnSpecBuilder setColumnType(JavaType columnType) {
        this.columnType = columnType;
        return this;
    }

    public ColumnSpecBuilder setJoinSpec(JoinSpec joinSpec) {
        this.joinSpec = joinSpec;
        return this;
    }

    public ColumnSpec createColumnSpec() {
        return new ColumnSpec(columnName, columnType, joinSpec);
    }
}