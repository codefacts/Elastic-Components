package elasta.orm.core;

import java.util.List;

public class FieldInfoBuilder {
    private String path;
    private List<String> fields;

    public FieldInfoBuilder setPath(String path) {
        this.path = path;
        return this;
    }

    public FieldInfoBuilder setFields(List<String> fields) {
        this.fields = fields;
        return this;
    }

    public FieldInfo createSqlField() {
        return new FieldInfo(path, fields);
    }
}