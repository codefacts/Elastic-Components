package elasta.orm.delete.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.builder.DeleteTableFunctionBuilderContext;
import elasta.orm.delete.ex.DeleteFuctionBuilderContextException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DeleteTableFunctionBuilderContextImpl implements DeleteTableFunctionBuilderContext {
    Map<String, Optional<DeleteTableFunction>> functionMap;

    public DeleteTableFunctionBuilderContextImpl(Map<String, Optional<DeleteTableFunction>> functionMap) {
        Objects.requireNonNull(functionMap);
        this.functionMap = functionMap;
    }

    @Override
    public boolean containsTable(String table) {
        return functionMap.containsKey(table);
    }

    @Override
    public boolean contains(String table) {
        return functionMap.containsKey(table) && functionMap.get(table).isPresent();
    }

    @Override
    public DeleteTableFunctionBuilderContext putEmpty(String table) {
        functionMap.put(table, Optional.empty());
        return this;
    }

    @Override
    public DeleteTableFunctionBuilderContext put(String table, DeleteTableFunction deleteTableFunction) {
        functionMap.put(
            table,
            Optional.of(deleteTableFunction)
        );
        return this;
    }

    @Override
    public DeleteTableFunction get(String table) {
        return getByTable(table).orElseThrow(() -> new DeleteFuctionBuilderContextException("No Delete Function exists for table '" + table + "'"));
    }

    private Optional<DeleteTableFunction> getByTable(String table) {
        Optional<DeleteTableFunction> deleteFunction = functionMap.get(table);
        if (deleteFunction == null) {
            throw new DeleteFuctionBuilderContextException("No Delete Function exists for table '" + table + "'");
        }
        return deleteFunction;
    }

    @Override
    public DeleteTableFunctionBuilderContext makeImmutable() {
        functionMap = ImmutableMap.copyOf(functionMap);
        return this;
    }

    @Override
    public boolean isEmpty(String dependentTable) {
        return functionMap.containsKey(dependentTable) && Utils.not(
            functionMap.get(dependentTable).isPresent()
        );
    }
}
