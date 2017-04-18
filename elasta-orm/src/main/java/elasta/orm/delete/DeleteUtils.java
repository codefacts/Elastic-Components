package elasta.orm.delete;

import elasta.orm.delete.loader.impl.DependencyInfo;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;
import elasta.orm.entity.core.columnmapping.IndirectRelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMappingOptions;
import elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions;
import elasta.orm.upsert.TableData;
import elasta.sql.core.ColumnValuePair;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteUtils {
    static ColumnValuePair[] columnValuePairs(TableData tableData) {
        final String[] primaryColumns = tableData.getPrimaryColumns();
        ColumnValuePair[] columnValuePairs = new ColumnValuePair[primaryColumns.length];
        for (int i = 0; i < primaryColumns.length; i++) {
            final String column = primaryColumns[i];
            columnValuePairs[i] = new ColumnValuePair(
                column,
                tableData.getValues().getValue(
                    column
                )
            );
        }
        return columnValuePairs;
    }

    static Stream<RelationMapping> getRelationMappingsForDelete(DbMapping dbMapping) {
        return Arrays.stream(dbMapping.getRelationMappings())
            .filter(relationMapping -> relationMapping.getOptions().getCascadeDelete() == RelationMappingOptions.CascadeDelete.YES);
    }

    static Stream<DependencyInfo> getTableDependenciesForDelete(List<DependencyInfo> dependencyInfos) {
        return dependencyInfos.stream()
            .filter(dependencyInfo -> {
                RelationMapping relationMapping = dependencyInfo.getRelationMapping();
                switch (relationMapping.getColumnType()) {
                    case DIRECT: {
                        DirectRelationMapping mapping = (DirectRelationMapping) relationMapping;
                        return mapping.getOptions().getLoadAndDeleteParent() == DirectRelationMappingOptions.LoadAndDeleteParent.YES;
                    }
                    case INDIRECT: {
                        IndirectRelationMapping mapping = (IndirectRelationMapping) relationMapping;
                        return mapping.getOptions().getLoadAndDeleteParent() == DirectRelationMappingOptions.LoadAndDeleteParent.YES;
                    }
                }
                return false;
            });
    }
}
