package elasta.orm.delete.impl;

import elasta.orm.delete.DeleteContext;import elasta.orm.delete.DeleteFunction;import elasta.orm.delete.IndirectDeleteHandler;import elasta.orm.delete.RelationTableDeleteFunction;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class IndirectDeleteHandlerImpl implements IndirectDeleteHandler {
    final RelationTableDeleteFunction relationTableDeleteFunction;
    final DeleteFunction deleteFunction;

    public IndirectDeleteHandlerImpl(RelationTableDeleteFunction relationTableDeleteFunction, DeleteFunction deleteFunction) {
        Objects.requireNonNull(relationTableDeleteFunction);
        Objects.requireNonNull(deleteFunction);
        this.relationTableDeleteFunction = relationTableDeleteFunction;
        this.deleteFunction = deleteFunction;
    }

    @Override
    public void delete(JsonObject parent, JsonObject jsonObject, DeleteContext deleteContext) {
        relationTableDeleteFunction.delete(parent, jsonObject, deleteContext);
        deleteFunction.delete(jsonObject, deleteContext);
    }
}
