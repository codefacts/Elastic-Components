package elasta.sql.core;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/26/2017.
 */
@Value
@Builder
final public class SqlCondition {
    final String column;
    final Object value;

    public SqlCondition(String column, Object value) {
        Objects.requireNonNull(column);
        Objects.requireNonNull(value);
        this.column = column;
        this.value = value;
    }
}
