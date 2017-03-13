package elasta.orm.nm.delete.dependency.loader.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.json.sql.DbSql;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoader;
import elasta.orm.nm.delete.dependency.loader.DependencyDataLoaderBuilder;
import elasta.orm.nm.delete.dependency.ex.DependencyDataLoaderException;
import elasta.orm.nm.entitymodel.EntityMappingHelper;
import elasta.orm.nm.entitymodel.ForeignColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.DbColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.DirectDbColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.IndirectDbColumnMapping;
import elasta.orm.nm.upsert.ColumnToColumnMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/5/2017.
 */
final public class DependencyDataLoaderBuilderImpl implements DependencyDataLoaderBuilder {
    final EntityMappingHelper helper;
    final DbSql dbSql;

    public DependencyDataLoaderBuilderImpl(EntityMappingHelper helper, DbSql dbSql) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(dbSql);
        this.helper = helper;
        this.dbSql = dbSql;
    }

    private DependencyDataLoader buildDependencyLoader(DependencyInfo dependencyInfo, List<DependencyInfo> dependentTableDependencies) {

        final String dependentTable = dependencyInfo.getDependentTable();

        String primaryColumn = helper.getDbMappingByTable(dependentTable).getPrimaryColumn();

        List<String> columns = createDependencyColumns(Collections.singletonList(primaryColumn), dependentTableDependencies);

        switch (dependencyInfo.getDbColumnMapping().getColumnType()) {
//            case INDIRECT: {
//                IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dependencyInfo.getDbColumnMapping();
//
//                ImmutableList.Builder<ColumnToColumnMapping> srcListBuilder = ImmutableList.builder();
//                ImmutableList.Builder<ColumnToColumnMapping> dstListBuilder = ImmutableList.builder();
//
//                for (int i = 0; i < mapping.getSrcForeignColumnMappingList().size(); i++) {
//
//                    final ForeignColumnMapping srcColumnMapping = mapping.getSrcForeignColumnMappingList().get(i);
//                    final ForeignColumnMapping dstColumnMapping = mapping.getDstForeignColumnMappingList().get(i);
//
//                    srcListBuilder.add(
//                        new ColumnToColumnMapping(
//                            srcColumnMapping.getSrcColumn().getName(),
//                            srcColumnMapping.getDstColumn().getName()
//                        )
//                    );
//                    dstListBuilder.add(
//                        new ColumnToColumnMapping(
//                            dstColumnMapping.getSrcColumn().getName(),
//                            dstColumnMapping.getDstColumn().getName()
//                        )
//                    );
//                }
//
//                ImmutableList<ColumnToColumnMapping> srcList = srcListBuilder.build();
//                ImmutableList<ColumnToColumnMapping> dstList = srcListBuilder.build();
//
//                return new IndirectDependencyDataLoaderImpl(
//                    dependentTable,
//                    mapping.getRelationTable(),
//                    srcList.toArray(new ColumnToColumnMapping[srcList.size()]),
//                    dstList.toArray(new ColumnToColumnMapping[dstList.size()]),
//                    new String[]{primaryColumn},
//                    columns.toArray(new String[columns.size()]),
//                    dbSql
//                );
//            }
            case DIRECT: {
                return new DependencyDataLoaderImpl(
                    dependentTable,
                    directColumnMappings(dependencyInfo.getDbColumnMapping()),
                    new String[]{primaryColumn},
                    columns.toArray(new String[columns.size()]),
                    dbSql
                );
            }
        }
        throw new DependencyDataLoaderException("Invalid dependencyInfo.getDbColumnMapping().getColumnType() '" + dependencyInfo.getDbColumnMapping().getColumnType() + "'");
    }

    public static List<String> createDependencyColumns(List<String> primaryColumns, List<DependencyInfo> dependentTableDependencies) {
        return ImmutableList.<String>builder().addAll(primaryColumns).addAll(Arrays.asList(dependencyColumns(dependentTableDependencies))).build();
    }

    private ColumnToColumnMapping[] directColumnMappings(DbColumnMapping dbColumnMapping) {
        switch (dbColumnMapping.getColumnType()) {
            case DIRECT: {
                ImmutableList.Builder<ColumnToColumnMapping> listBuilder = ImmutableList.builder();
                DirectDbColumnMapping mapping = (DirectDbColumnMapping) dbColumnMapping;
                mapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                    listBuilder.add(
                        new ColumnToColumnMapping(
                            foreignColumnMapping.getSrcColumn().getName(),
                            foreignColumnMapping.getDstColumn().getName()
                        )
                    );
                });
                ImmutableList<ColumnToColumnMapping> list = listBuilder.build();
                return list.toArray(new ColumnToColumnMapping[list.size()]);
            }
        }
        throw new DependencyDataLoaderException("Invalid db column mapping '" + dbColumnMapping + "'");
    }

    private static String[] dependencyColumns(List<DependencyInfo> dependencyTables) {

        ImmutableList.Builder<String> columnListBuilder = ImmutableList.builder();

        dependencyTables.forEach(dependencyInfo -> {
            switch (dependencyInfo.getDbColumnMapping().getColumnType()) {
                case INDIRECT: {
                    IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dependencyInfo.getDbColumnMapping();
                    mapping.getDstForeignColumnMappingList().forEach(foreignColumnMapping -> {
                        columnListBuilder.add(
                            foreignColumnMapping.getSrcColumn().getName()
                        );
                    });
                }
                break;
                case DIRECT:
                    DirectDbColumnMapping mapping = (DirectDbColumnMapping) dependencyInfo.getDbColumnMapping();
                    mapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                        columnListBuilder.add(
                            foreignColumnMapping.getDstColumn().getName()
                        );
                    });
                    break;
            }
        });

        ImmutableList<String> list = columnListBuilder.build();
        return list.toArray(new String[list.size()]);
    }

    @Override
    public DependencyDataLoader build(DependencyInfo dependencyInfo, List<DependencyInfo> dependentTableDependencies) {
        return buildDependencyLoader(dependencyInfo, dependentTableDependencies);
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderBuilderImpl{" +
            "helper=" + helper +
            ", dbSql=" + dbSql +
            '}';
    }
}