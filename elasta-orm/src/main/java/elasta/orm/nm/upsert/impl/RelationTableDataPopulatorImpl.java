package elasta.orm.nm.upsert.impl;

import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.RelationTableDataPopulator;
import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.HashSet;

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
    public TableData populate(TableData srcTableData, TableData dstTableData) {

        final JsonObject srcValues = srcTableData.getValues();
        final JsonObject dstValues = dstTableData.getValues();

        HashMap<String, Object> map = new HashMap<>();

        for (ColumnToColumnMapping srcMapping : srcMappings) {
            map.put(
                srcMapping.getDstColumn(),
                srcValues.getValue(
                    srcMapping.getSrcColumn()
                )
            );
        }

        for (ColumnToColumnMapping dstMapping : dstMappings) {
            map.put(
                dstMapping.getDstColumn(),
                dstValues.getValue(
                    dstMapping.getSrcColumn()
                )
            );
        }

        return new TableData(relationTable,
            map.keySet().toArray(
                new String[map.size()]
            ),
            new JsonObject(map)
        );
    }

    public static void main(String[] args) {

    }
}
