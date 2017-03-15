package elasta.orm.delete.impl;

import elasta.sql.core.ColumnValuePair;import elasta.orm.delete.DeleteContext;import elasta.sql.core.DeleteData;import elasta.orm.delete.RelationTableDeleteFunction;
import elasta.orm.upsert.RelationTableDataPopulator;
import elasta.orm.upsert.TableData;
import elasta.orm.upsert.TableDataPopulator;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/07.
 */
final public class RelationTableDeleteFunctionImpl implements RelationTableDeleteFunction {
    final RelationTableDataPopulator relationTableDataPopulator;
    final TableDataPopulator srcTableDataPopulator;
    final TableDataPopulator dstTableDataPopulator;

    public RelationTableDeleteFunctionImpl(RelationTableDataPopulator relationTableDataPopulator, TableDataPopulator srcTableDataPopulator, TableDataPopulator dstTableDataPopulator) {
        Objects.requireNonNull(relationTableDataPopulator);
        Objects.requireNonNull(srcTableDataPopulator);
        Objects.requireNonNull(dstTableDataPopulator);
        this.relationTableDataPopulator = relationTableDataPopulator;
        this.srcTableDataPopulator = srcTableDataPopulator;
        this.dstTableDataPopulator = dstTableDataPopulator;
    }

    @Override
    public void delete(JsonObject parent, JsonObject jsonObject, DeleteContext deleteContext) {

        final TableData tableData = relationTableDataPopulator.populate(
            srcTableDataPopulator.populate(parent),
            dstTableDataPopulator.populate(jsonObject)
        );

        List<ColumnValuePair> pairs = Arrays.asList(tableData.getPrimaryColumns()).stream()
            .map(column -> new ColumnValuePair(column, tableData.getValues().getValue(column)))
            .collect(Collectors.toList());

        deleteContext.add(
            new DeleteData(tableData.getTable(), pairs.toArray(new ColumnValuePair[pairs.size()]))
        );
    }
}
