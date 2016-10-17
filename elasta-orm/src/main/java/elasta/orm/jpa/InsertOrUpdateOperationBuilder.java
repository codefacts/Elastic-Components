package elasta.orm.jpa;

import io.vertx.core.json.JsonObject;

public class InsertOrUpdateOperationBuilder {
    private boolean insert;
    private String table;
    private String primaryKey;
    private RelationTableColumns relationTableColumns;
    private JsonObject data;

    public InsertOrUpdateOperationBuilder setInsert(boolean insert) {
        this.insert = insert;
        return this;
    }

    public InsertOrUpdateOperationBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public InsertOrUpdateOperationBuilder setPrimaryColumn(String primaryKey) {
        this.primaryKey = primaryKey;
        return this;
    }

    public InsertOrUpdateOperationBuilder setRelationTableColumns(RelationTableColumns relationTableColumns) {
        this.relationTableColumns = relationTableColumns;
        return this;
    }

    public InsertOrUpdateOperationBuilder setData(JsonObject data) {
        this.data = data;
        return this;
    }

    public DbImpl.InsertOrUpdateOperation createInsertOrUpdateOperation() {
        return new DbImpl.InsertOrUpdateOperation(insert, table, primaryKey, relationTableColumns, data);
    }
}