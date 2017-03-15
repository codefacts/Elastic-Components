package elasta.orm.upsert.impl;

import elasta.orm.upsert.*;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-10.
 */
final public class RelationTableUpserFunctionImpl implements RelationTableUpserFunction {
    final RelationTableDataPopulator relationTableDataPopulator;

    public RelationTableUpserFunctionImpl(RelationTableDataPopulator relationTableDataPopulator) {
        Objects.requireNonNull(relationTableDataPopulator);
        this.relationTableDataPopulator = relationTableDataPopulator;
    }

    @Override
    public TableData upsert(TableData srcTableData, TableData dstTableData, UpsertContext upsertContext) {

        final TableData tableData = relationTableDataPopulator.populate(srcTableData, dstTableData);

        upsertContext.putOrMerge(
            UpsertUtils.toTableAndPrimaryColumnsKey(
                tableData.getTable(),
                tableData.getPrimaryColumns(),
                tableData.getValues()
            ),
            tableData
        );

        return tableData;
    }
}
