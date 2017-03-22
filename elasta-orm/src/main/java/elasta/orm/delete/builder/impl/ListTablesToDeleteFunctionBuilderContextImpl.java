package elasta.orm.delete.builder.impl;

import elasta.commons.Utils;
import elasta.orm.delete.ListTablesToDeleteFunction;
import elasta.orm.delete.builder.ListTablesToDeleteFunctionBuilderContext;
import elasta.orm.delete.builder.ex.ListTablesToDeleteFunctionBuilderContextException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/22/2017.
 */
final public class ListTablesToDeleteFunctionBuilderContextImpl implements ListTablesToDeleteFunctionBuilderContext {
    final Map<String, Optional<ListTablesToDeleteFunction>> functionMap;

    public ListTablesToDeleteFunctionBuilderContextImpl(Map<String, Optional<ListTablesToDeleteFunction>> functionMap) {
        Objects.requireNonNull(functionMap);
        this.functionMap = functionMap;
    }

    @Override
    public boolean contains(String entity) {
        return functionMap.containsKey(entity);
    }

    @Override
    public ListTablesToDeleteFunction get(String entity) {
        return getFunctionOptional(entity).orElseThrow(() -> new ListTablesToDeleteFunctionBuilderContextException("No ListTablesToDeleteFunction found for entity '" + entity + "'"));
    }

    private Optional<ListTablesToDeleteFunction> getFunctionOptional(String entity) {
        Optional<ListTablesToDeleteFunction> function = functionMap.get(entity);
        if (function == null) {
            throw new ListTablesToDeleteFunctionBuilderContextException("No ListTablesToDeleteFunction found for entity '" + entity + "'");
        }
        return function;
    }

    @Override
    public void putEmpty(String entity) {
        functionMap.put(entity, Optional.empty());
    }

    @Override
    public void put(String entity, ListTablesToDeleteFunction listTablesToDeleteFunction) {
        functionMap.put(entity, Optional.of(listTablesToDeleteFunction));
    }

    @Override
    public boolean isEmpty(String referencingEntity) {
        Optional<ListTablesToDeleteFunction> function = functionMap.get(referencingEntity);
        return function != null && Utils.not(function.isPresent());
    }
}
