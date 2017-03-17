package elasta.orm.delete.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.core.touple.immutable.Tpls;
import elasta.orm.delete.*;
import elasta.orm.delete.loader.MultiTableDependenciesLoader;
import elasta.orm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/17/2017.
 */
final public class DeleteFunctionImpl implements DeleteFunction {
    final ListTablesToDeleteFunction listTablesToDeleteFunction;
    final MultiTableDependenciesLoader multiTableDependenciesLoader;
    final ReloadTableDataFunction reloadTableDataFunction;
    final DeleteTableFunction deleteTableFunction;

    public DeleteFunctionImpl(ListTablesToDeleteFunction listTablesToDeleteFunction, MultiTableDependenciesLoader multiTableDependenciesLoader, ReloadTableDataFunction reloadTableDataFunction, DeleteTableFunction deleteTableFunction) {
        Objects.requireNonNull(listTablesToDeleteFunction);
        Objects.requireNonNull(multiTableDependenciesLoader);
        Objects.requireNonNull(reloadTableDataFunction);
        Objects.requireNonNull(deleteTableFunction);
        this.listTablesToDeleteFunction = listTablesToDeleteFunction;
        this.multiTableDependenciesLoader = multiTableDependenciesLoader;
        this.reloadTableDataFunction = reloadTableDataFunction;
        this.deleteTableFunction = deleteTableFunction;
    }

    @Override
    public Promise<JsonObject> delete(JsonObject entity, DeleteContext deleteContext) {

        return reloadTableDataFunction
            .reloadIfNecessary(
                toTableData(entity)
            )
            .mapP(
                (parentTableDataList) ->
                    multiTableDependenciesLoader
                        .load(parentTableDataList)
                        .map(tableToTableDataMap -> Tpls.of(
                            tableToTableDataMap,
                            parentTableDataList
                        ))
            )
            .then(tpl2 -> tpl2.accept(
                (tableToTableDataMap, tableDatas) -> tableDatas.forEach(
                    tableData -> deleteTableFunction
                        .delete(tableData, deleteContext, tableToTableDataMap)
                ))
            ).map(tpl2 -> entity);
    }

    private Set<TableData> toTableData(JsonObject entity) {
        final Set<TableData> tableDataSet = new LinkedHashSet<>();

        listTablesToDeleteFunction.listTables(entity, new ListTablesToDeleteContextImpl(
            tableDataSet
        ));

        return tableDataSet;
    }
}
