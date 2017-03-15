package elasta.orm.delete.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.delete.TableToTableDependenciesMap;
import elasta.orm.delete.loader.DependencyDataLoader;import elasta.orm.delete.loader.DependencyDataLoaderBuilder;
import elasta.orm.delete.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.loader.DependencyDataLoaderGraphBuilder;
import elasta.orm.upsert.UpsertTest;

import java.util.Collection;
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
                UpsertTest.dbSql("orm")
            )
        );

        TableToTableDependenciesMap map = mapBuilder.build();

        System.out.println(map);

        DependencyDataLoaderGraph graph = graphBuilder.build(map);

        System.out.println(graph);
    }
}
