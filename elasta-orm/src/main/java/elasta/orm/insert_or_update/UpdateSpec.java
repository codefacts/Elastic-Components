package elasta.orm.insert_or_update;

import java.util.List;

/**
 * Created by Jango on 9/24/2016.
 */
public class UpdateSpec {
    private final String sql;
    private final List<Object> params;

    public UpdateSpec(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getParams() {
        return params;
    }
}
