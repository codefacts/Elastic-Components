package elasta.orm.sql.query;

import java.util.List;

public class SqlFieldBuilder {
    private String path;
    private List<String> fields;

    public SqlFieldBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public SqlFieldBuilder setFields(List<String> fields) {
        this.fields = fields;
        return this;
    }

    public SqlField createSqlField() {
        return new SqlField(path, fields);
    }
}