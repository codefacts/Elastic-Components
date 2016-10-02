package elasta.orm.json.sql.core;

/**
 * Created by Jango on 9/15/2016.
 */
public class ColumnSpec {
    private final String columnName;
    private final JoinSpec joinSpec;

    public ColumnSpec(String columnName, JoinSpec joinSpec) {
        this.columnName = columnName;
        this.joinSpec = joinSpec;
    }

    public String getColumnName() {
        return columnName;
    }

    public JoinSpec getJoinSpec() {
        return joinSpec;
    }
}
