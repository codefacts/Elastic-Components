package elasta.orm.delete.impl;

import elasta.core.promise.intfs.Promise;
import elasta.core.touple.immutable.Tpls;
import elasta.orm.delete.*;
import elasta.orm.delete.loader.MultiTableDependenciesLoader;
import elasta.orm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by sohan on 3/17/2017.
 */
final public class DeleteFunctionImpl implements DeleteFunction {
    final ListTablesToDeleteFunction listTablesToDeleteFunction;
    final MultiTableDependenciesLoader multiTableDependenciesLoader;
    final ReloadTableDataFunction reloadTableDataFunction;
    final TableToDeleteTableFunctionMap tableToDeleteTableFunctionMap;

    public DeleteFunctionImpl(ListTablesToDeleteFunction listTablesToDeleteFunction, MultiTableDependenciesLoader multiTableDependenciesLoader, ReloadTableDataFunction reloadTableDataFunction, TableToDeleteTableFunctionMap tableToDeleteTableFunctionMap) {
        Objects.requireNonNull(listTablesToDeleteFunction);
        Objects.requireNonNull(multiTableDependenciesLoader);
        Objects.requireNonNull(reloadTableDataFunction);
        Objects.requireNonNull(tableToDeleteTableFunctionMap);
        this.listTablesToDeleteFunction = listTablesToDeleteFunction;
        this.multiTableDependenciesLoader = multiTableDependenciesLoader;
        this.reloadTableDataFunction = reloadTableDataFunction;
        this.tableToDeleteTableFunctionMap = tableToDeleteTableFunctionMap;
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
                    tableData -> tableToDeleteTableFunctionMap.get(tableData.getTable())
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

    public static void main(String[] asdf) {

    }
}
