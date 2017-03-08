package elasta.orm.nm.delete.dependency.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Promise;
import elasta.orm.json.sql.*;
import elasta.orm.json.sql.core.JoinType;
import elasta.orm.nm.delete.dependency.DependencyDataLoader;
import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.TableData;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/6/2017.
 */
final public class IndirectDependencyDataLoaderImpl implements DependencyDataLoader {
    final String dependentTable;
    final String relationTable;
    final ColumnToColumnMapping[] srcColumnMappings;
    final ColumnToColumnMapping[] dstColumnMappings;
    final String[] primaryColumns;
    final String[] columns;
    final DbSql dbSql;

    public IndirectDependencyDataLoaderImpl(String dependentTable, String relationTable, ColumnToColumnMapping[] srcColumnMappings, ColumnToColumnMapping[] dstColumnMappings, String[] primaryColumns, String[] columns, DbSql dbSql) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(relationTable);
        Objects.requireNonNull(srcColumnMappings);
        Objects.requireNonNull(dstColumnMappings);
        Objects.requireNonNull(primaryColumns);
        Objects.requireNonNull(columns);
        Objects.requireNonNull(dbSql);
        this.dependentTable = dependentTable;
        this.relationTable = relationTable;
        this.srcColumnMappings = srcColumnMappings;
        this.dstColumnMappings = dstColumnMappings;
        this.primaryColumns = primaryColumns;
        this.columns = columns;
        this.dbSql = dbSql;
    }

    @Override
    public String dependentTable() {
        return dependentTable;
    }

    @Override
    public Promise<List<TableData>> load(TableData parentTableData) {

        final String alias = "t";
        final String relationTableAlias = "r";

        ImmutableList.Builder<SqlCriteria> listBuilder = ImmutableList.builder();

        for (ColumnToColumnMapping dstColumnMapping : dstColumnMappings) {
            listBuilder.add(
                new SqlCriteria(
                    dstColumnMapping.getDstColumn(),
                    parentTableData.getValues().getValue(dstColumnMapping.getSrcColumn()),
                    Optional.of(relationTableAlias)
                )
            );
        }

        return dbSql.queryJo(
            sqlSelections(alias, primaryColumns, columns),
            new SqlFrom(dependentTable, Optional.of(alias)),
            sqlJoins(relationTable, alias, relationTableAlias, dstColumnMappings),
            listBuilder.build()
        ).map(jsonObjects -> jsonObjects.stream().map(jo -> new TableData(dependentTable, primaryColumns, jo)).collect(Collectors.toList()));
    }

    private List<SqlJoin> sqlJoins(String relationTable, String parentAlias, String alias, ColumnToColumnMapping[] dstColumnMappings) {
        ImmutableList.Builder<SqlJoinColumn> listBuilder = ImmutableList.builder();
        for (ColumnToColumnMapping dstColumnMapping : dstColumnMappings) {
            listBuilder.add(
                new SqlJoinColumn(
                    dstColumnMapping.getDstColumn(),
                    dstColumnMapping.getSrcColumn(),
                    Optional.of(parentAlias)
                )
            );
        }
        return Arrays.asList(
            new SqlJoin(
                JoinType.INNER_JOIN,
                relationTable,
                Optional.of(alias),
                listBuilder.build()
            )
        );
    }

    private List<SqlSelection> sqlSelections(String alias, String[] primaryColumns, String[] columns) {
        ImmutableList.Builder<SqlSelection> listBuilder = ImmutableList.builder();
        for (String primaryColumn : primaryColumns) {
            listBuilder.add(
                new SqlSelection(primaryColumn, Optional.of(alias))
            );
        }
        for (String column : columns) {
            listBuilder.add(
                new SqlSelection(column, Optional.of(alias))
            );
        }
        return listBuilder.build();
    }
}
