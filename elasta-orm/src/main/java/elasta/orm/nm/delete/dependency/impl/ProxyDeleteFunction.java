package elasta.orm.nm.delete.dependency.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.dependency.DeleteFunction;
import elasta.orm.nm.delete.dependency.DeleteFunctionBuilderContext;
import elasta.orm.nm.upsert.TableData;

import java.util.List;
import java.util.Map;
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
    public void delete(TableData tableData, DeleteContext context, Map<String, List<TableData>> tableToTableDataMap) {
        this.context.get(dependentTable).delete(tableData, context, tableToTableDataMap);
    }
}
