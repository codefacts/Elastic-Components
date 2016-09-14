package elasta.orm;

/**
 * Created by Jango on 9/14/2016.
 */
final public class JoinSpec {
    private final String column;
    private final String joinTable;
    private final String joinColumn;

    public JoinSpec(String column, String joinTable, String joinColumn) {
        this.column = column;
        this.joinTable = joinTable;
        this.joinColumn = joinColumn;
    }

    public String getJoinTable() {
        return joinTable;
    }

    public String getJoinColumn() {
        return joinColumn;
    }
}
