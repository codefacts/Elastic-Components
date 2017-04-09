package elasta.orm.relation.delete.impl;

import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
@Value
final public class ColumnToFieldMapping {
    final String field;
    final String column;

    public ColumnToFieldMapping(String field, String column) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(column);
        this.field = field;
        this.column = column;
    }
}
