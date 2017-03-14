package elasta.orm.sql.sql.core;

import io.vertx.core.json.JsonObject;

import java.util.Collection;

public class UpdateTplBuilder {
    private UpdateOperationType updateOperationType;
    private String table;
    private JsonObject data;
    private Collection<SqlCriteria> sqlCriterias;

    public UpdateTplBuilder setUpdateOperationType(UpdateOperationType updateOperationType) {
        this.updateOperationType = updateOperationType;
        return this;
    }

    public UpdateTplBuilder setTable(String table) {
        this.table = table;
        return this;
    }

    public UpdateTplBuilder setData(JsonObject data) {
        this.data = data;
        return this;
    }

    public UpdateTplBuilder setSqlCriterias(Collection<SqlCriteria> sqlCriterias) {
        this.sqlCriterias = sqlCriterias;
        return this;
    }

    public UpdateTpl createUpdateTpl() {
        return new UpdateTpl(updateOperationType, table, data, sqlCriterias);
    }
}