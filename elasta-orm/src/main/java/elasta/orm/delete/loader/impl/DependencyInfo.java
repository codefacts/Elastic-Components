package elasta.orm.delete.loader.impl;

import elasta.orm.entity.core.columnmapping.RelationMapping;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/5/2017.
 */
@Value
final public class DependencyInfo {
    final String dependentTable;
    final RelationMapping relationMapping;

    public DependencyInfo(String dependentTable, RelationMapping relationMapping) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(relationMapping);
        this.dependentTable = dependentTable;
        this.relationMapping = relationMapping;
    }
}
