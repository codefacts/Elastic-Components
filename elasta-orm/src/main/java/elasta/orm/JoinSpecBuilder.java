package elasta.orm;

final public class JoinSpecBuilder {
    private String column;
    private String joinTable;
    private String joinColumn;

    public JoinSpecBuilder setColumn(String column) {
        this.column = column;
        return this;
    }

    public JoinSpecBuilder setJoinTable(String joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public JoinSpecBuilder setJoinColumn(String joinColumn) {
        this.joinColumn = joinColumn;
        return this;
    }

    public JoinSpec createJoinSepc() {
        return new JoinSpec(column, joinTable, joinColumn);
    }
}