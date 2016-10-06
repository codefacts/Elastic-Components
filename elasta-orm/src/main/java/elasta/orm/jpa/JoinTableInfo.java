package elasta.orm.jpa;

/**
 * Created by Jango on 10/6/2016.
 */
public class JoinTableInfo {
    private final String joinTable;
    private final String joinColumn;

    public JoinTableInfo(String joinTable, String joinColumn) {
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
