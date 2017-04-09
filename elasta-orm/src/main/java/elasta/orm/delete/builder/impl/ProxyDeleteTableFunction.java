package elasta.orm.delete.builder.impl;

import elasta.orm.delete.DeleteContext;
import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.TableToTableDataMap;
import elasta.orm.event.builder.BuilderContext;
import elasta.orm.upsert.TableData;

import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class ProxyDeleteTableFunction implements DeleteTableFunction {
    final String dependentTable;
    final BuilderContext<DeleteTableFunction> context;

    public ProxyDeleteTableFunction(String dependentTable, BuilderContext<DeleteTableFunction> context) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(context);
        this.dependentTable = dependentTable;
        this.context = context;
    }

    @Override
    public void delete(TableData tableData, DeleteContext context, TableToTableDataMap tableToTableDataMap) {
        this.context.get(dependentTable).delete(tableData, context, tableToTableDataMap);
    }
}
