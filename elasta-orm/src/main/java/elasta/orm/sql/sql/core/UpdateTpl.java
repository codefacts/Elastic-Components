package elasta.orm.sql.sql.core;

import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by Jango on 10/8/2016.
 */
final public class UpdateTpl {
    private final UpdateOperationType updateOperationType;
    private final String table;
    private final JsonObject data;
    private final Collection<SqlCriteria> sqlCriterias;

    public UpdateTpl(UpdateOperationType updateOperationType, String table, JsonObject data, Collection<SqlCriteria> sqlCriterias) {
        Objects.requireNonNull(updateOperationType);
        Objects.requireNonNull(table);
        Objects.requireNonNull(data);
        Objects.requireNonNull(sqlCriterias);
        this.updateOperationType = updateOperationType;
        this.table = table;
        this.data = data;
        this.sqlCriterias = sqlCriterias;
    }

    public UpdateOperationType getUpdateOperationType() {
        return updateOperationType;
    }

    public String getTable() {
        return table;
    }

    public JsonObject getData() {
        return data;
    }

    public Collection<SqlCriteria> getSqlCriterias() {
        return sqlCriterias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateTpl updateTpl = (UpdateTpl) o;

        if (updateOperationType != updateTpl.updateOperationType) return false;
        if (table != null ? !table.equals(updateTpl.table) : updateTpl.table != null) return false;
        if (data != null ? !data.equals(updateTpl.data) : updateTpl.data != null) return false;
        return sqlCriterias != null ? sqlCriterias.equals(updateTpl.sqlCriterias) : updateTpl.sqlCriterias == null;
    }

    @Override
    public int hashCode() {
        int result = updateOperationType != null ? updateOperationType.hashCode() : 0;
        result = 31 * result + (table != null ? table.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (sqlCriterias != null ? sqlCriterias.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UpdateTpl{" +
            "updateOperationType=" + updateOperationType +
            ", table='" + table + '\'' +
            ", data=" + data +
            ", sqlCriterias=" + sqlCriterias +
            '}';
    }
}
