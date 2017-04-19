package elasta.orm.delete.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.delete.DeleteUtils;
import elasta.orm.delete.TableToTableDependenciesMap;
import elasta.orm.delete.loader.DependencyDataLoader;
import elasta.orm.delete.loader.DependencyDataLoaderBuilder;
import elasta.orm.delete.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.loader.DependencyDataLoaderGraphBuilder;
import elasta.orm.entity.core.RelationType;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions;
import elasta.orm.entity.core.columnmapping.RelationMapping;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/5/2017.
 */
final public class DependencyDataLoaderGraphBuilderImpl implements DependencyDataLoaderGraphBuilder {
    final DependencyDataLoaderBuilder dependencyDataLoaderBuilder;

    public DependencyDataLoaderGraphBuilderImpl(DependencyDataLoaderBuilder dependencyDataLoaderBuilder) {
        Objects.requireNonNull(dependencyDataLoaderBuilder);
        this.dependencyDataLoaderBuilder = dependencyDataLoaderBuilder;
    }

    public DependencyDataLoaderGraph build(TableToTableDependenciesMap tableToTableDependenciesMap) {

        ImmutableMap.Builder<String, Collection<DependencyDataLoader>> tableToDependencyDataLoadersMapBuilder = ImmutableMap.builder();

        tableToTableDependenciesMap.forEach((table, tableDependencies) -> {

            tableToDependencyDataLoadersMapBuilder.put(table, dependencyDataLoaders(tableDependencies));
        });

        return new DependencyDataLoaderGraphImpl(
            tableToDependencyDataLoadersMapBuilder.build()
        );
    }

    private List<DependencyDataLoader> dependencyDataLoaders(List<DependencyInfo> tableDependencies) {

        ImmutableList.Builder<DependencyDataLoader> dependencyDataLoaderListBuilder = ImmutableList.builder();

        DeleteUtils.getTableDependenciesForLoadAndDelete(tableDependencies)
            .filter(dependencyInfo -> {
                if (dependencyInfo.getRelationMapping().getColumnType() == RelationType.DIRECT) {
                    DirectRelationMapping mapping = (DirectRelationMapping) dependencyInfo.getRelationMapping();
                    return mapping.getOptions().getLoadAndDeleteParent() == DirectRelationMappingOptions.LoadAndDeleteParent.LOAD_AND_DELETE;
                }
                return false;
            })
            .forEach(
                dependencyInfo -> dependencyDataLoaderListBuilder.add(
                    dependencyDataLoaderBuilder.build(dependencyInfo)
                )
            );

        return dependencyDataLoaderListBuilder.build();
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderGraphBuilderImpl{" +
            "dependencyDataLoaderBuilder=" + dependencyDataLoaderBuilder +
            '}';
    }

    public static void main(String[] asfd) {
    }
}
