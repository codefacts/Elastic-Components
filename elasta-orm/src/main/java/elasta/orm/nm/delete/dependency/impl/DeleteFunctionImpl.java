package elasta.orm.nm.delete.dependency.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.DeleteData;
import elasta.orm.nm.delete.dependency.DeleteFunction;
import elasta.orm.nm.delete.DeleteUtils;
import elasta.orm.nm.delete.dependency.DirectDependencyDeleteHandler;
import elasta.orm.nm.delete.dependency.IndirectDependencyDeleteHandler;
import elasta.orm.nm.upsert.TableData;

import java.util.List;
import java.util.Map;
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
    public void delete(TableData tableData, DeleteContext context, Map<String, List<TableData>> tableToTableDataMap) {

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
