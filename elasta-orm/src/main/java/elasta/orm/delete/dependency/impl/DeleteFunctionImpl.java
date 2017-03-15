package elasta.orm.delete.dependency.impl;

import elasta.orm.delete.DeleteContext;import elasta.sql.core.DeleteData;import elasta.orm.delete.DeleteUtils;import elasta.orm.delete.dependency.DirectDependencyDeleteHandler;import elasta.orm.delete.dependency.IndirectDependencyDeleteHandler;import elasta.orm.delete.dependency.TableToTableDataMap;
import elasta.orm.delete.dependency.DeleteFunction;
import elasta.orm.upsert.TableData;

import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DeleteFunctionImpl implements DeleteFunction {
    final DirectDependencyDeleteHandler[] directDependencyDeleteHandlers;
    final IndirectDependencyDeleteHandler[] indirectDependencyDeleteHandlers;

    public DeleteFunctionImpl(DirectDependencyDeleteHandler[] directDependencyDeleteHandlers, IndirectDependencyDeleteHandler[] indirectDependencyDeleteHandlers) {
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
