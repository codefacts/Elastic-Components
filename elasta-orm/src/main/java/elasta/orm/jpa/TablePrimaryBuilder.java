package elasta.orm.jpa;

public class TablePrimaryBuilder {
    private String table;
    private String primaryKey;

    public TablePrimaryBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public TablePrimaryBuilder setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public TablePrimary createTablePrimary() {
        return new TablePrimary(table, primaryKey);
    }
}