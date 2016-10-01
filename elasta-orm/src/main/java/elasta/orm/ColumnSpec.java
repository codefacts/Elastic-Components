package elasta.orm;

/**
 * Created by Jango on 9/15/2016.
 */
public class ColumnSpec {
    private final String columnName;
    private final JavaType columnType;
    private final JoinSpec joinSpec;

    public ColumnSpec(String columnName, JavaType columnType, JoinSpec joinSpec) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.joinSpec = joinSpec;
    }

    public String getColumnName() {
        return columnName;
    }

    public JavaType getColumnType() {
        return columnType;
    }

    public JoinSpec getJoinSpec() {
        return joinSpec;
    }
}
