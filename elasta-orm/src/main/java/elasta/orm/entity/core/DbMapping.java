package elasta.orm.entity.core;

import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-08.
 */
@Value
@Builder
final public class DbMapping {
    final String table;
    final String primaryColumn;
    final ColumnMapping[] columnMappings;
    final RelationMapping[] relationMappings;

    public DbMapping(String table, String primaryColumn, ColumnMapping[] columnMappings, RelationMapping[] relationMappings) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumn);
        Objects.requireNonNull(columnMappings);
        Objects.requireNonNull(relationMappings);
        this.table = table;
        this.primaryColumn = primaryColumn;
        this.columnMappings = columnMappings;
        this.relationMappings = relationMappings;
    }
}
