package elasta.orm.delete.loader.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.orm.delete.loader.DependencyDataLoader;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.RelationType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.entity.core.columnmapping.IndirectRelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.sql.core.ColumnToColumnMapping;
import elasta.sql.SqlDB;
import elasta.orm.delete.loader.DependencyDataLoaderBuilder;
import elasta.orm.delete.ex.DependencyDataLoaderException;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;

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

    private DependencyDataLoader buildDependencyLoader(DependencyInfo dependencyInfo) {

        final String dependentTable = dependencyInfo.getDependentTable();

        switch (dependencyInfo.getRelationMapping().getColumnType()) {
            case DIRECT: {
                return new DependencyDataLoaderImpl(
                    dependentTable,
                    directColumnMappings(dependencyInfo.getRelationMapping()),
                    new String[]{
                        helper.getDbMappingByTable(dependentTable).getPrimaryColumn()
                    },
                    createDependencyColumns(helper.getDbMappingByTable(dependentTable)),
                    sqlDB
                );
            }
        }
        throw new DependencyDataLoaderException("Invalid dependencyInfo.getRelationMapping().getColumnType() '" + dependencyInfo.getRelationMapping().getColumnType() + "'");
    }

    public static Set<String> createDependencyColumns(DbMapping dbMapping) {
        return ImmutableSet.<String>builder()
            .add(dbMapping.getPrimaryColumn())
            .addAll(
                Arrays.stream(dbMapping.getRelationMappings())
                    .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == RelationType.DIRECT || dbColumnMapping.getColumnType() == RelationType.INDIRECT)
                    .flatMap(dbColumnMapping -> {
                        switch (dbColumnMapping.getColumnType()) {
                            case DIRECT: {
                                return ((DirectRelationMapping) dbColumnMapping).getForeignColumnMappingList().stream()
                                    .map(ForeignColumnMapping::getSrcColumn);
                            }
                            case INDIRECT: {
                                return ((IndirectRelationMapping) dbColumnMapping).getSrcForeignColumnMappingList().stream()
                                    .map(ForeignColumnMapping::getSrcColumn);
                            }
                        }
                        return Stream.empty();
                    })
                    .collect(Collectors.toList())
            )
            .build();
    }

    private ColumnToColumnMapping[] directColumnMappings(RelationMapping relationMapping) {
        switch (relationMapping.getColumnType()) {
            case DIRECT: {
                ImmutableList.Builder<ColumnToColumnMapping> listBuilder = ImmutableList.builder();
                DirectRelationMapping mapping = (DirectRelationMapping) relationMapping;
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
        throw new DependencyDataLoaderException("Invalid db column mapping '" + relationMapping + "'");
    }

    @Override
    public DependencyDataLoader build(DependencyInfo dependencyInfo) {
        return buildDependencyLoader(dependencyInfo);
    }

    @Override
    public String toString() {
        return "DependencyDataLoaderBuilderImpl{" +
            "helper=" + helper +
            ", sqlDB=" + sqlDB +
            '}';
    }
}