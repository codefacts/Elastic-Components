package elasta.orm.delete.impl;

import elasta.orm.delete.*;
import elasta.orm.upsert.TableData;

import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DeleteTableFunctionImpl implements DeleteTableFunction {
    final DirectRelationDeleteHandler[] directRelationDeleteHandlers;
    final DirectDependencyDeleteHandler[] directDependencyDeleteHandlers;
    final IndirectDependencyDeleteHandler[] indirectDependencyDeleteHandlers;

    public DeleteTableFunctionImpl(DirectRelationDeleteHandler[] directRelationDeleteHandlers, DirectDependencyDeleteHandler[] directDependencyDeleteHandlers, IndirectDependencyDeleteHandler[] indirectDependencyDeleteHandlers) {
        Objects.requireNonNull(directRelationDeleteHandlers);
        Objects.requireNonNull(directDependencyDeleteHandlers);
        Objects.requireNonNull(indirectDependencyDeleteHandlers);
        this.directRelationDeleteHandlers = directRelationDeleteHandlers;
        this.directDependencyDeleteHandlers = directDependencyDeleteHandlers;
        this.indirectDependencyDeleteHandlers = indirectDependencyDeleteHandlers;
    }

    @Override
    public void delete(TableData tableData, DeleteContext context, TableToTableDataMap tableToTableDataMap) {

        Objects.requireNonNull(tableData);
        Objects.requireNonNull(context);
        Objects.requireNonNull(tableToTableDataMap);

        for (DirectRelationDeleteHandler directRelationDeleteHandler : directRelationDeleteHandlers) {
            directRelationDeleteHandler.deleteRelation(tableData, context);
        }

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
