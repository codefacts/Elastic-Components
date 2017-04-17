package elasta.orm.delete.loader.impl;

import elasta.orm.delete.TableToTableDependenciesMap;
import elasta.orm.delete.impl.TableToTableDependenciesMapImpl;
import elasta.orm.delete.loader.TableToTableDependenciesMapBuilder;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.columnmapping.IndirectRelationMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;

import java.util.*;

/**
 * Created by sohan on 3/5/2017.
 */
final public class TableToTableDependenciesMapBuilderImpl implements TableToTableDependenciesMapBuilder {
    final EntityMappingHelper helper;

    public TableToTableDependenciesMapBuilderImpl(EntityMappingHelper helper) {
        this.helper = helper;
    }

    public TableToTableDependenciesMap build() {
        List<String> tables = helper.getTables();
        Map<String, Set<DependencyInfo>> map = new HashMap<>();
        tables.forEach(table -> createDependencyInfos(table, map));
        return new TableToTableDependenciesMapImpl(map);
    }

    private void createDependencyInfos(String table, Map<String, Set<DependencyInfo>> map) {
        final DbMapping dbMapping = helper.getDbMappingByTable(table);
        Arrays.stream(dbMapping.getRelationMappings())
            .forEach(
                relationMapping -> putInMap(
                    map, relationMapping.getReferencingTable(), new DependencyInfo(table, relationMapping)
                )
            );
    }

    private void putInMap(Map<String, Set<DependencyInfo>> map, final String referencingTable, DependencyInfo dependencyInfo) {
        Set<DependencyInfo> dependencyTables = map.get(referencingTable);
        if (dependencyTables == null) {
            map.put(referencingTable, dependencyTables = new LinkedHashSet<>());
        }
        dependencyTables.add(dependencyInfo);
    }

    public static void main(String[] args) {
    }

    @Override
    public String toString() {
        return "TableToTableDependenciesMapBuilderImpl{" +
            "helper=" + helper +
            '}';
    }
}
