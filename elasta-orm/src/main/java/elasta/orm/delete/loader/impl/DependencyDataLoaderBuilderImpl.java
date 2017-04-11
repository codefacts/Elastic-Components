package elasta.orm.delete.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.orm.delete.loader.DependencyDataLoader;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.entity.core.columnmapping.IndirectDbColumnMapping;
import elasta.sql.core.ColumnToColumnMapping;
import elasta.sql.SqlDB;
import elasta.orm.delete.loader.DependencyDataLoaderBuilder;
import elasta.orm.delete.ex.DependencyDataLoaderException;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.DirectDbColumnMapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by sohan on 3/5/2017.
 */
final public class DependencyDataLoaderBuilderImpl implements DependencyDataLoaderBuilder {
    final EntityMappingHelper helper;
    final SqlDB sqlDB;

    public DependencyDataLoaderBuilderImpl(EntityMappingHelper helper, SqlDB sqlDB) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(sqlDB);
        this.helper = helper;
        this.sqlDB = sqlDB;
    }

    private DependencyDataLoader buildDependencyLoader(DependencyInfo dependencyInfo, List<DependencyInfo> dependentTableDependencies) {

        final String dependentTable = dependencyInfo.getDependentTable();

        String primaryColumn = helper.getDbMappingByTable(dependentTable).getPrimaryColumn();

        Set<String> columns = createDependencyColumns(helper.getDbMappingByTable(dependentTable));

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
//                    sqlDB
//                );
//            }
            case DIRECT: {
                return new DependencyDataLoaderImpl(
                    dependentTable,
                    directColumnMappings(dependencyInfo.getDbColumnMapping()),
                    new String[]{primaryColumn},
                    columns,
                    sqlDB
                );
            }
        }
        throw new DependencyDataLoaderException("Invalid dependencyInfo.getDbColumnMapping().getColumnType() '" + dependencyInfo.getDbColumnMapping().getColumnType() + "'");
    }

    public static Set<String> createDependencyColumns(DbMapping dbMapping) {
        return ImmutableSet.<String>builder()
            .add(dbMapping.getPrimaryColumn())
            .addAll(
                Arrays.stream(dbMapping.getDbColumnMappings())
                    .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.DIRECT || dbColumnMapping.getColumnType() == ColumnType.INDIRECT)
                    .flatMap(dbColumnMapping -> {
                        switch (dbColumnMapping.getColumnType()) {
                            case DIRECT: {
                                return ((DirectDbColumnMapping) dbColumnMapping).getForeignColumnMappingList().stream()
                                    .map(ForeignColumnMapping::getSrcColumn);
                            }
                            case INDIRECT: {
                                return ((IndirectDbColumnMapping) dbColumnMapping).getSrcForeignColumnMappingList().stream()
                                    .map(ForeignColumnMapping::getSrcColumn);
                            }
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toList())
            )
            .build();
    }

    private ColumnToColumnMapping[] directColumnMappings(DbColumnMapping dbColumnMapping) {
        switch (dbColumnMapping.getColumnType()) {
            case DIRECT: {
                ImmutableList.Builder<ColumnToColumnMapping> listBuilder = ImmutableList.builder();
                DirectDbColumnMapping mapping = (DirectDbColumnMapping) dbColumnMapping;
                mapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                    listBuilder.add(
                        new ColumnToColumnMapping(
                            foreignColumnMapping.getSrcColumn(),
                            foreignColumnMapping.getDstColumn()
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

        ImmutableSet.Builder<String> columnListBuilder = ImmutableSet.builder();

        dependencyTables.forEach(dependencyInfo -> {
            switch (dependencyInfo.getDbColumnMapping().getColumnType()) {
                case INDIRECT: {
                    IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dependencyInfo.getDbColumnMapping();
                    mapping.getDstForeignColumnMappingList().forEach(foreignColumnMapping -> {
                        columnListBuilder.add(
                            foreignColumnMapping.getSrcColumn()
                        );
                    });
                }
                break;
                case DIRECT:
                    DirectDbColumnMapping mapping = (DirectDbColumnMapping) dependencyInfo.getDbColumnMapping();
                    mapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
                        columnListBuilder.add(
                            foreignColumnMapping.getDstColumn()
                        );
                    });
                    break;
            }
        });

        ImmutableSet<String> columns = columnListBuilder.build();
        return columns.toArray(new String[columns.size()]);
    }

    @Override
    public DependencyDataLoader build(DependencyInfo dependencyInfo, List<DependencyInfo> dependentTableDependencies) {
        return buildDependencyLoader(dependencyInfo, dependentTableDependencies);
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderBuilderImpl{" +
            "helper=" + helper +
            ", sqlDB=" + sqlDB +
            '}';
    }
}