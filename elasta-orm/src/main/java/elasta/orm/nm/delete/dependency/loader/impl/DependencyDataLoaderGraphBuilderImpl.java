package elasta.orm.nm.delete.dependency.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoader;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoaderBuilder;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoaderGraph;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoaderGraphBuilder;
import elasta.orm.nm.upsert.UpsertTest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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

    public DependencyDataLoaderGraph build(Map<String, List<DependencyInfo>> tableToTableDependenciesMap) {

        ImmutableMap.Builder<String, Collection<DependencyDataLoader>> tableToDependencyDataLoadersMapBuilder = ImmutableMap.builder();

        tableToTableDependenciesMap.forEach((table, tableDependencies) -> {

            ImmutableList.Builder<DependencyDataLoader> listBuilder = ImmutableList.builder();

            tableDependencies.forEach(dependencyInfo -> {

//                if (not(tableToTableDependenciesMap.containsKey(dependencyInfo.getDependentTable()))) {
//                    return;
//                }

                listBuilder.add(
                    dependencyDataLoaderBuilder.build(dependencyInfo, tableToTableDependenciesMap.get(dependencyInfo.getDependentTable()))
                );
            });

            tableToDependencyDataLoadersMapBuilder.put(table, listBuilder.build());
        });

        return new DependencyDataLoaderGraphImpl(
            tableToDependencyDataLoadersMapBuilder.build()
        );
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderGraphBuilderImpl{" +
            "dependencyDataLoaderBuilder=" + dependencyDataLoaderBuilder +
            '}';
    }

    public static void main(String[] asfd) {
        TableToTableDependenciesMapBuilderImpl mapBuilder = new TableToTableDependenciesMapBuilderImpl(
            UpsertTest.helper()
        );
        DependencyDataLoaderGraphBuilderImpl graphBuilder = new DependencyDataLoaderGraphBuilderImpl(
            new DependencyDataLoaderBuilderImpl(
                UpsertTest.helper(),
                UpsertTest.dbSql("nm")
            )
        );

        Map<String, List<DependencyInfo>> map = mapBuilder.build();

        System.out.println(map);

        DependencyDataLoaderGraph graph = graphBuilder.build(map);

        System.out.println(graph);
    }
}
