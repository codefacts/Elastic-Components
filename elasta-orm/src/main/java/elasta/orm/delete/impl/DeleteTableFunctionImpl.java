package elasta.orm.delete.impl;

import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.DirectDependencyDeleteHandler;
import elasta.orm.delete.IndirectDependencyDeleteHandler;
import elasta.orm.delete.DeleteContext;import elasta.sql.core.DeleteData;import elasta.orm.delete.DeleteUtils;
import elasta.orm.delete.TableToTableDataMap;
import elasta.orm.upsert.TableData;

import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DeleteTableFunctionImpl implements DeleteTableFunction {
    final DirectDependencyDeleteHandler[] directDependencyDeleteHandlers;
    final IndirectDependencyDeleteHandler[] indirectDependencyDeleteHandlers;

    public DeleteTableFunctionImpl(DirectDependencyDeleteHandler[] directDependencyDeleteHandlers, IndirectDependencyDeleteHandler[] indirectDependencyDeleteHandlers) {
        Objects.requireNonNull(directDependencyDeleteHandlers);
        Objects.requireNonNull(indirectDependencyDeleteHandlers);
        this.directDependencyDeleteHandlers = directDependencyDeleteHandlers;
        this.indirectDependencyDeleteHandlers = indirectDependencyDeleteHandlers;
    }

    @Override
    public void delete(TableData tableData, DeleteContext context, TableToTableDataMap tableToTableDataMap) {

        Objects.requireNonNull(tableData);
        Objects.requireNonNull(context);
        Objects.requireNonNull(tableToTableDataMap);

        for (DirectDependencyDeleteHandler directDependencyDeleteHandler : directDependencyDeleteHandlers) {
            directDependencyDeleteHandler.delete(tableData, context, tableToTableDataMap);
        }

        for (IndirectDependencyDeleteHandler indirectDependencyDeleteHandler : indirectDependencyDeleteHandlers) {
            indirectDependencyDeleteHandler.delete(tableData, context);
        }

        context.add(
            new DeleteData(
                tableData.getTable(),
                DeleteUtils.columnValuePairs(tableData)
            )
        );
    }

}
