package elasta.orm.entity;

import elasta.orm.entity.core.Entity;

import java.util.*;

/**
 * Created by sohan on 3/17/2017.
 */
final public class TableDependency {
    final Map<String, DependencyTpl> tableToDependencyInfoMap;

    public TableDependency(Map<String, DependencyTpl> tableToDependencyInfoMap) {
        Objects.requireNonNull(tableToDependencyInfoMap);
        this.tableToDependencyInfoMap = tableToDependencyInfoMap;
    }

    public Map<String, DependencyTpl> getTableToDependencyInfoMap() {
        return tableToDependencyInfoMap;
    }

    void add(Entity entity, DependencyInfo dependencyInfo) {
        String table = entity.getDbMapping().getTable();
        DependencyTpl dependencyTpl = tableToDependencyInfoMap.get(table);
        if (dependencyTpl == null) {
            tableToDependencyInfoMap.put(
                table,
                dependencyTpl = new DependencyTpl(entity, new HashMap<>())
            );
        }

        dependencyTpl.add(dependencyInfo);
    }
}
