package elasta.sql.core;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
@Value
@Builder
final public class JoinData {
    final String parentAlias;
    final JoinType joinType;
    final String table;
    final String alias;
    final List<ColumnToColumnMapping> columnToColumnMappings;

    public JoinData(String parentAlias, JoinType joinType, String table, String alias, List<ColumnToColumnMapping> columnToColumnMappings) {
        Objects.requireNonNull(parentAlias);
        Objects.requireNonNull(joinType);
        Objects.requireNonNull(table);
        Objects.requireNonNull(alias);
        Objects.requireNonNull(columnToColumnMappings);
        this.parentAlias = parentAlias;
        this.joinType = joinType;
        this.table = table;
        this.alias = alias;
        this.columnToColumnMappings = columnToColumnMappings;
    }
}
