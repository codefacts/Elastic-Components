package elasta.orm;

public class ColumnSpecBuilder {
    private String columnName;
    private JavaType javaType;
    private JoinSpec joinSpec;

    public ColumnSpecBuilder setColumnName(String columnName) {
        this.columnName = columnName;
        return this;
    }

    public ColumnSpecBuilder setJavaType(JavaType javaType) {
        this.javaType = javaType;
        return this;
    }

    public ColumnSpecBuilder setJoinSpec(JoinSpec joinSpec) {
        this.joinSpec = joinSpec;
        return this;
    }

    public ColumnSpec createColumnSpec() {
        return new ColumnSpec(columnName, javaType, joinSpec);
    }
}