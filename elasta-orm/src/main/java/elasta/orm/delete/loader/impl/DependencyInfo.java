package elasta.orm.delete.loader.impl;

import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/5/2017.
 */
@Value
final public class DependencyInfo {
    final String dependentTable;
    final DbColumnMapping dbColumnMapping;

    public DependencyInfo(String dependentTable, DbColumnMapping dbColumnMapping) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(dbColumnMapping);
        this.dependentTable = dependentTable;
        this.dbColumnMapping = dbColumnMapping;
    }
}
