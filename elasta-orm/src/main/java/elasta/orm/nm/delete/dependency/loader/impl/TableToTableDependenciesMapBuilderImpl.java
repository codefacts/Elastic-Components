package elasta.orm.nm.delete.dependency.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.nm.EntityUtils;
import elasta.orm.nm.delete.dependency.TableToTableDependenciesMap;
import elasta.orm.nm.delete.dependency.TableToTableDependenciesMapImpl;
import elasta.orm.nm.delete.dependency.loader.TableToTableDependenciesMapBuilder;
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
final public class TableToTableDependenciesMapBuilderImpl implements TableToTableDependenciesMapBuilder {
    final EntityMappingHelper helper;

    public TableToTableDependenciesMapBuilderImpl(EntityMappingHelper helper) {
        this.helper = helper;
    }

    public TableToTableDependenciesMap build() {
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
        return new TableToTableDependenciesMapImpl(map);
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
        TableToTableDependenciesMap map = new TableToTableDependenciesMapBuilderImpl(helper).build();
        System.out.println(map);
    }

    @Override
    public String toString() {
        return "TableToTableDependenciesMapBuilderImpl{" +
            "helper=" + helper +
            '}';
    }
}
