package elasta.orm.delete.impl;

import lombok.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by Jango on 17/02/07.
 */
@Value
final public class DeleteData {
    final OperationType operationType;
    final String table;
    final ColumnAndOptionalValuePair[] updateValues;
    final ColumnValuePair[] whereCriteria;

    public DeleteData(String table, ColumnValuePair[] whereCriteria) {
        this(OperationType.DELETE, table, new ColumnAndOptionalValuePair[]{}, whereCriteria);
    }

    public DeleteData(OperationType operationType, String table, ColumnAndOptionalValuePair[] updateValues, ColumnValuePair[] whereCriteria) {
        Objects.requireNonNull(operationType);
        Objects.requireNonNull(table);
        Objects.requireNonNull(updateValues);
        Objects.requireNonNull(whereCriteria);
        this.operationType = operationType;
        this.table = table;
        this.updateValues = updateValues;
        this.whereCriteria = whereCriteria;
    }

    public enum OperationType {UPDATE, DELETE}

    @Value
    public static class ColumnAndOptionalValuePair {
        final String column;
        final Object value;

        public ColumnAndOptionalValuePair(String column) {
            Objects.requireNonNull(column);
            this.column = column;
            this.value = null;
        }

        public ColumnAndOptionalValuePair(String column, Object value) {
            Objects.requireNonNull(column);
            Objects.requireNonNull(value);
            this.column = column;
            this.value = value;
        }

        public Optional<Object> getValue() {
            return Optional.ofNullable(value);
        }
    }
}
