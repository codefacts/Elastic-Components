package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.DeleteFunction;
import elasta.orm.nm.delete.DirectDeleteHandler;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
public class DirectDeleteHandlerImpl implements DirectDeleteHandler {
    final DeleteFunction deleteFunction;

    public DirectDeleteHandlerImpl(DeleteFunction deleteFunction) {
        Objects.requireNonNull(deleteFunction);
        this.deleteFunction = deleteFunction;
    }

    @Override
    public void delete(JsonObject jsonObject, DeleteContext deleteContext) {
        deleteFunction.delete(jsonObject, deleteContext);
    }
}
