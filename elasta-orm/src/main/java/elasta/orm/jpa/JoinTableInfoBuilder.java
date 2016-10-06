package elasta.orm.jpa;

public class JoinTableInfoBuilder {
    private String joinTable;
    private String joinColumn;

    public JoinTableInfoBuilder setJoinTable(String joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public JoinTableInfoBuilder setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
        return this;
    }

    public JoinTableInfo createJoinTableInfo() {
        return new JoinTableInfo(joinTable, joinColumn);
    }
}