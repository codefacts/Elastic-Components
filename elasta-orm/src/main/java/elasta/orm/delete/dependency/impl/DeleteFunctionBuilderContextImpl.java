package elasta.orm.delete.dependency.impl;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.delete.dependency.DeleteFunctionBuilderContext;import elasta.orm.delete.dependency.ex.DeleteFuctionBuilderContextException;import elasta.orm.delete.dependency.DeleteFunction;
import elasta.orm.delete.dependency.DeleteFunctionBuilderContext;
import elasta.orm.delete.dependency.ex.DeleteFuctionBuilderContextException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DeleteFunctionBuilderContextImpl implements DeleteFunctionBuilderContext {
    Map<String, Optional<DeleteFunction>> functionMap;

    public DeleteFunctionBuilderContextImpl(Map<String, Optional<DeleteFunction>> functionMap) {
        Objects.requireNonNull(functionMap);
        this.functionMap = functionMap;
    }

    @Override
    public boolean containsTable(String table) {
        return functionMap.containsKey(table);
    }

    @Override
    public boolean contains(String table) {
        return functionMap.containsKey(table) && getByTable(table).isPresent();
    }

    @Override
    public DeleteFunctionBuilderContext putEmpty(String table) {
        functionMap.put(table, Optional.empty());
        return this;
    }

    @Override
    public DeleteFunctionBuilderContext put(String table, DeleteFunction deleteFunction) {
        functionMap.put(
            table,
            Optional.of(deleteFunction)
        );
        return this;
    }

    @Override
    public DeleteFunction get(String table) {
        return getByTable(table).orElseThrow(() -> new DeleteFuctionBuilderContextException("No Delete Function exists for table '" + table + "'"));
    }

    private Optional<DeleteFunction> getByTable(String table) {
        Optional<DeleteFunction> deleteFunction = functionMap.get(table);
        if (deleteFunction == null) {
            throw new DeleteFuctionBuilderContextException("No Delete Function exists for table '" + table + "'");
        }
        return deleteFunction;
    }

    @Override
    public DeleteFunctionBuilderContext makeImmutable() {
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
