package elasta.orm;

/**
 * Created by Jango on 9/15/2016.
 */
public class ColumnSpec {
    private final String columnName;
    private final String name;
    private final JavaType javaType;
    private final JoinSpec joinSpec;

    public ColumnSpec(String columnName, String name, JavaType javaType, JoinSpec joinSpec) {
        this.columnName = columnName;
        this.name = name;
        this.javaType = javaType;
        this.joinSpec = joinSpec;
    }

    public String getName() {
        return name;
    }

    public String getColumnName() {
        return columnName;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public JoinSpec getJoinSpec() {
        return joinSpec;
    }
}
