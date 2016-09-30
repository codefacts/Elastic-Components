package elasta.orm;

import java.util.List;

public class UpdateSpecBuilder {
    private String sql;
    private List<Object> params;

    public UpdateSpecBuilder setSql(String sql) {
        this.sql = sql;
        return this;
    }

    public UpdateSpecBuilder setParams(List<Object> params) {
        this.params = params;
        return this;
    }

    public UpdateSpec createUpdateSpec() {
        return new UpdateSpec(sql, params);
    }
}