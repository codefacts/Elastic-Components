package elasta.orm.delete.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.TableToDeleteTableFunctionMap;
import elasta.orm.delete.ex.TableToDeleteTableFunctionMapException;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/25/2017.
 */
final public class TableToDeleteTableFunctionMapImpl implements TableToDeleteTableFunctionMap {
    final Map<String, DeleteTableFunction> functionMap;

    public TableToDeleteTableFunctionMapImpl(Map<String, DeleteTableFunction> functionMap) {
        Objects.requireNonNull(functionMap);
        this.functionMap = ImmutableMap.copyOf(functionMap);
    }

    @Override
    public DeleteTableFunction get(String table) {
        DeleteTableFunction deleteTableFunction = functionMap.get(table);
        if (deleteTableFunction == null) {
            throw new TableToDeleteTableFunctionMapException("No DeleteTableFunction exists for table '" + table + "'");
        }
        return deleteTableFunction;
    }
}
