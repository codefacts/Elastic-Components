package elasta.orm.nm.delete.dependency.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.nm.EntityUtils;
import elasta.orm.nm.entitymodel.DbMapping;
import elasta.orm.nm.entitymodel.EntityMappingHelper;
import elasta.orm.nm.entitymodel.columnmapping.DirectDbColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.IndirectDbColumnMapping;
import elasta.orm.nm.entitymodel.impl.EntityMappingHelperImpl;
import elasta.orm.nm.upsert.UpsertTest;

import java.util.*;

/**
 * Created by sohan on 3/5/2017.
 */
final public class TableToTableDependenciesMapBuilder {
    final EntityMappingHelper helper;

    public TableToTableDependenciesMapBuilder(EntityMappingHelper helper) {
        this.helper = helper;
    }

    public Map<String, List<DependencyInfo>> build() {
        Set<String> tables = helper.getTables();
        Map<String, List<DependencyInfo>> map = new HashMap<>();
        tables.forEach(table -> {
            final DbMapping dbMapping = helper.getDbMappingByTable(table);
            Arrays.stream(dbMapping.getDbColumnMappings())
                .forEach(dbColumnMapping -> {
                    switch (dbColumnMapping.getColumnType()) {
                        case INDIRECT: {
                            IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dbColumnMapping;
                            putInMap(map, mapping.getReferencingTable(), new DependencyInfo(table, dbColumnMapping));
                        }
                        break;
                        case DIRECT: {
                            DirectDbColumnMapping mapping = (DirectDbColumnMapping) dbColumnMapping;
                            putInMap(map, mapping.getReferencingTable(), new DependencyInfo(table, dbColumnMapping));
                        }
                        break;
                    }
                });
        });
        return toImmutable(map);
    }

    private Map<String, List<DependencyInfo>> toImmutable(Map<String, List<DependencyInfo>> map) {
        ImmutableMap.Builder<String, List<DependencyInfo>> mapBuilder = ImmutableMap.builder();
        map.forEach((key, values) -> mapBuilder.put(key, ImmutableList.copyOf(values)));
        return mapBuilder.build();
    }

    private void putInMap(Map<String, List<DependencyInfo>> map, final String referencingTable, DependencyInfo dependencyInfo) {
        List<DependencyInfo> dependencyTables = map.get(referencingTable);
        if (dependencyTables == null) {
            map.put(referencingTable, dependencyTables = new ArrayList<>());
        }
        dependencyTables.add(dependencyInfo);
    }

    public static void main(String[] args) {
        EntityMappingHelperImpl helper = new EntityMappingHelperImpl(
            EntityUtils.toEntityNameToEntityMap(
                UpsertTest.entities()
            )
        );
        Map<String, List<DependencyInfo>> map = new TableToTableDependenciesMapBuilder(helper).build();
        System.out.println(map);
    }

    @Override
    public String toString() {
        return "TableToTableDependenciesMapBuilder{" +
            "helper=" + helper +
            '}';
    }
}
