package elasta.sql.core;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 5/13/2017.
 */
@Value
@Builder
final public class SqlPagination {
    final ColumnAliasPair paginationColumn;
    final long offset;
    final int size;

    SqlPagination(ColumnAliasPair paginationColumn, long offset, int size) {
        Objects.requireNonNull(paginationColumn);
        Objects.requireNonNull(offset);
        Objects.requireNonNull(size);
        this.paginationColumn = paginationColumn;
        this.offset = offset;
        this.size = size;
    }
}
