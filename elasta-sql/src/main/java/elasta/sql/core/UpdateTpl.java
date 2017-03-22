package elasta.sql.core;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by Jango on 10/8/2016.
 */
@Value
@Builder
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
}
