package elasta.orm.nm.upsert.impl;

import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.RelationTableDataPopulator;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-10.
 */
public class RelationTableDataPopulatorImpl implements RelationTableDataPopulator {
    private final String srcTable;
    private final String srcTablePrimaryColumn;
    private final String dstTable;
    private final String dstTablePrimaryColumn;
    private final String relationTable;
    private final ColumnToColumnMapping[] srcMappings;
    private final ColumnToColumnMapping[] dstMappings;

    public RelationTableDataPopulatorImpl(String srcTable, String srcTablePrimaryColumn, String dstTable, String dstTablePrimaryColumn, String relationTable, ColumnToColumnMapping[] srcMappings, ColumnToColumnMapping[] dstMappings) {
        this.srcTable = srcTable;
        this.srcTablePrimaryColumn = srcTablePrimaryColumn;
        this.dstTable = dstTable;
        this.dstTablePrimaryColumn = dstTablePrimaryColumn;
        this.relationTable = relationTable;
        this.srcMappings = srcMappings;
        this.dstMappings = dstMappings;
    }

    @Override
    public TableData populate(JsonObject src, JsonObject dst) {
        return null;
    }

    public static void main(String[] args) {

    }
}
