package elasta.orm.delete.dependency.impl;

import elasta.orm.delete.DeleteContext;
import elasta.orm.delete.dependency.DeleteFunction;
import elasta.orm.delete.dependency.DeleteFunctionBuilderContext;
import elasta.orm.delete.dependency.TableToTableDataMap;
import elasta.orm.upsert.TableData;

import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class ProxyDeleteFunction implements DeleteFunction {
    final String dependentTable;
    final DeleteFunctionBuilderContext context;

    public ProxyDeleteFunction(String dependentTable, DeleteFunctionBuilderContext context) {
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
