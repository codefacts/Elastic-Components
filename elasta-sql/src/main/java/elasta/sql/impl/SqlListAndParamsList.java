package elasta.sql.impl;

import io.vertx.core.json.JsonArray;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/25/2017.
 */
@Value
@Builder
public class SqlListAndParamsList {
    final List<String> sqlList;
    final List<JsonArray> paramsList;

    public SqlListAndParamsList(List<String> sqlList, List<JsonArray> paramsList) {
        Objects.requireNonNull(sqlList);
        Objects.requireNonNull(paramsList);
        this.sqlList = sqlList;
        this.paramsList = paramsList;
    }
}
