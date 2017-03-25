package elasta.orm.delete.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.delete.ReloadTableDataFunction;
import elasta.orm.delete.TableToTableDependenciesMap;
import elasta.orm.delete.ex.ReloadDependencyValuesFunctionException;
import elasta.orm.delete.loader.impl.DependencyDataLoaderBuilderImpl;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.upsert.TableData;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/13/2017.
 */
final public class ReloadTableDataFunctionImpl implements ReloadTableDataFunction {
    final EntityMappingHelper helper;
    final TableToTableDependenciesMap tableToTableDependenciesMap;
    final SqlDB sqlDB;

    public ReloadTableDataFunctionImpl(EntityMappingHelper helper, TableToTableDependenciesMap tableToTableDependenciesMap, SqlDB sqlDB) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(tableToTableDependenciesMap);
        Objects.requireNonNull(sqlDB);
        this.helper = helper;
        this.tableToTableDependenciesMap = tableToTableDependenciesMap;
        this.sqlDB = sqlDB;
    }

    @Override
    public Promise<List<TableData>> reloadIfNecessary(Collection<TableData> tableDataList) {

        List<Promise<TableData>> promiseList = tableDataList.stream().map(tableData -> {
            Set<String> columns = DependencyDataLoaderBuilderImpl.createDependencyColumns(
                helper.getDbMappingByTable(tableData.getTable())
            );

            boolean reloadTableData = false;

            for (String column : columns) {
                reloadTableData = reloadTableData || tableData.getValues().getValue(column) == null;
            }

            if (Utils.not(reloadTableData)) {
                return Promises.of(tableData);
            }

            return sqlDB.query(tableData.getTable(), columns, primaryColumnValuesCriteria(tableData))
                .map(ResultSet::getRows)
                .map(jsonObjects -> {
                    if (jsonObjects.size() < 1) {
                        return tableData.getValues();
//                        throw new ReloadDependencyValuesFunctionException("No data found for tableData '" + tableData.toString() + "'");
                    }
                    return jsonObjects.get(0);
                })
                .map(jsonObject -> new TableData(
                    tableData.getTable(),
                    tableData.getPrimaryColumns(),
                    jsonObject
                ))
                ;

        }).collect(Collectors.toList());

        return Promises.when(promiseList);
    }

    private JsonObject primaryColumnValuesCriteria(TableData tableData) {
        final ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();

        for (String primaryColumn : tableData.getPrimaryColumns()) {
            mapBuilder.put(primaryColumn, tableData.getValues().getValue(primaryColumn));
        }

        return new JsonObject(
            mapBuilder.build()
        );
    }

    public static void main(String[] asdf) {

    }
}
