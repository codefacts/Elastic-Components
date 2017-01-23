package elasta.orm.nm.upsert.builder;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.nm.upsert.UpsertFunction;
import elasta.orm.nm.upsert.ex.MissingUpsertFunctionException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 2017-01-23.
 */
final public class FunctionMapImpl implements FunctionMap {
    private Map<String, Optional<UpsertFunction>> functionMap;

    public FunctionMapImpl() {
        functionMap = new HashMap<>();
    }

    public FunctionMapImpl(Map<String, Optional<UpsertFunction>> functionMap) {
        this.functionMap = functionMap;
    }

    @Override
    public FunctionMap putEmpty(String entity) {
        functionMap.put(entity, Optional.empty());
        return this;
    }

    @Override
    public FunctionMap put(String entity, UpsertFunction upsertFunction) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(upsertFunction);
        functionMap.put(entity, Optional.of(upsertFunction));
        return this;
    }

    @Override
    public boolean exists(String entity) {
        return functionMap.containsKey(entity);
    }

    @Override
    public UpsertFunction get(String entity) {
        return functionMap.get(entity).get();
    }

    @Override
    public FunctionMap makeImmutable() {
        functionMap.forEach((entity, upsertFunction) -> {
            if (not(upsertFunction.isPresent())) {
                throw new MissingUpsertFunctionException("Upsert Function is missing for entity '" + entity + "'");
            }
        });
        functionMap = ImmutableMap.copyOf(functionMap);
        return this;
    }
}
