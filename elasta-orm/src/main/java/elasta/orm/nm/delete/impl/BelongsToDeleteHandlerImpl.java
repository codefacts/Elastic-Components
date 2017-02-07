package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.BelongsToDeleteHandler;
import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.DeleteFunction;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
public class BelongsToDeleteHandlerImpl implements BelongsToDeleteHandler {
    final DeleteFunction deleteFunction;

    public BelongsToDeleteHandlerImpl(DeleteFunction deleteFunction) {
        Objects.requireNonNull(deleteFunction);
        this.deleteFunction = deleteFunction;
    }

    @Override
    public void delete(JsonObject parent, JsonObject jsonObject, DeleteContext deleteContext) {
        deleteFunction.delete(jsonObject, deleteContext);
    }
}
