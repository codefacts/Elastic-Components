package elasta.orm.nm.delete.dependency;

import elasta.core.promise.intfs.Promise;
import elasta.orm.json.sql.DbSql;
import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.TableData;

import java.util.List;
import java.util.Objects;

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

        
        return null;
    }
}
