package elasta.orm.jpa;

public class TableIdPairBuilder {
    private String table;
    private Object id;
    private boolean relationTable;
    private RelationTableInfo relationTableInfo;

    public TableIdPairBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public TableIdPairBuilder setId(Object id) {
        this.id = id;
        return this;
    }

    public TableIdPairBuilder setRelationTable(boolean relationTable) {
        this.relationTable = relationTable;
        return this;
    }

    public TableIdPairBuilder setRelationTableInfo(RelationTableInfo relationTableInfo) {
        this.relationTableInfo = relationTableInfo;
        return this;
    }

    public TableIdPair createTableIdPair() {
        return new TableIdPair(table, id, relationTable, relationTableInfo);
    }
}