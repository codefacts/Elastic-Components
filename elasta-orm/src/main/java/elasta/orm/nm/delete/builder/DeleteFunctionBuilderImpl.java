package elasta.orm.nm.delete.builder;

import elasta.orm.nm.delete.DeleteFunction;
import elasta.orm.nm.entitymodel.EntityMappingHelper;
import elasta.orm.nm.upsert.builder.FunctionMap;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class DeleteFunctionBuilderImpl implements DeleteFunctionBuilder {
    final EntityMappingHelper helper;
    final FunctionMap<DeleteFunction> functionMap;

    public DeleteFunctionBuilderImpl(EntityMappingHelper helper, FunctionMap<DeleteFunction> functionMap) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(functionMap);
        this.helper = helper;
        this.functionMap = functionMap;
    }

    @Override
    public DeleteFunction create(String entity) {

        return null;
    }
}
