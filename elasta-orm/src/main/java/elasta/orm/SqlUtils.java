package elasta.orm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/19/2016.
 */
public class SqlUtils {

    public static List<TableSpec> makeDefaults(List<TableSpec> tableSpecs) {
        ImmutableList.Builder<TableSpec> builder = ImmutableList.builder();
        for (TableSpec tableSpec : tableSpecs) {

            String tableAlias = tableSpec.getTableAlias() == null ? tableSpec.getTableName() : tableSpec.getTableAlias();

            List<ColumnSpec> columnSpecs = tableSpec.getColumnSpecs();

            ImmutableList.Builder<ColumnSpec> colBuilder = ImmutableList.builder();

            for (ColumnSpec columnSpec : columnSpecs) {
                colBuilder.add(
                    new ColumnSpecBuilder()
                        .setColumnType(columnSpec.getColumnType())
                        .setColumnName(columnSpec.getColumnName())
                        .setJoinSpec(
                            columnSpec.getJoinSpec() == null ? null : new JoinSpecBuilder()
                                .setJoinType(columnSpec.getJoinSpec().getJoinType() == null ? JoinType.INNER_JOIN : columnSpec.getJoinSpec().getJoinType())
                                .setJoinTable(columnSpec.getJoinSpec().getJoinTable())
                                .setJoinTableAlias(columnSpec.getJoinSpec().getJoinTableAlias() == null ? columnSpec.getJoinSpec().getJoinTable() : columnSpec.getJoinSpec().getJoinTableAlias())
                                .setJoinColumn(columnSpec.getJoinSpec().getJoinColumn() == null ? columnSpec.getColumnName() : columnSpec.getJoinSpec().getJoinColumn())
                                .createJoinSpec()
                        )
                        .createColumnSpec()
                );
            }

            builder.add(
                new TableSpecBuilder()
                    .setTableName(tableSpec.getTableName())
                    .setTableAlias(tableAlias)
                    .setColumnSpecs(colBuilder.build())
                    .createTableSpec()
            );
        }
        return builder.build();
    }

    public static Map<String, Map<String, JoinSpec>> toJoinSpecsByTableNameMap(Map<String, TableSpec> tableSpecMap) {

        ImmutableMap.Builder<String, Map<String, JoinSpec>> mapBuilder = ImmutableMap.builder();

        tableSpecMap.entrySet().forEach(entry -> {

            TableSpec tableSpec = entry.getValue();

            ImmutableMap.Builder<String, JoinSpec> builder = ImmutableMap.builder();
            tableSpec.getColumnSpecs().forEach(columnSpec -> {
                if (columnSpec.getJoinSpec() != null) {
                    builder.put(columnSpec.getColumnName(), columnSpec.getJoinSpec());
                }
            });

            mapBuilder.put(entry.getKey(), builder.build());
        });

        return mapBuilder.build();
    }

    public static Map<String, TableSpec> toTableSpecByTableMap(List<TableSpec> tableSpecs) {
        ImmutableMap.Builder<String, TableSpec> builder = ImmutableMap.builder();
        for (TableSpec tableSpec : tableSpecs) {

            builder.put(tableSpec.getTableName(), tableSpec);
        }
        return builder.build();
    }

    public static Map<String, Map<String, ColumnSpec>> toColumnSpecMapByColumnMap(Map<String, TableSpec> tableSpecMap) {

        ImmutableMap.Builder<String, Map<String, ColumnSpec>> columnMapByNameMapBuilder = ImmutableMap.builder();

        tableSpecMap.entrySet().forEach(entry -> {

            ImmutableMap.Builder<String, ColumnSpec> mapBuilder = ImmutableMap.builder();

            for (ColumnSpec columnSpec : entry.getValue().getColumnSpecs()) {
                mapBuilder.put(columnSpec.getColumnName(), columnSpec);
            }

            columnMapByNameMapBuilder.put(entry.getKey(), mapBuilder.build());
        });

        return columnMapByNameMapBuilder.build();
    }
}
