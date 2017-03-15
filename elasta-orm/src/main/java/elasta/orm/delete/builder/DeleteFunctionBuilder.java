package elasta.orm.delete.builder;

import elasta.orm.delete.DeleteFunction;

/**
 * Created by Jango on 17/02/07.
 */
public interface DeleteFunctionBuilder {
    DeleteFunction create(String entity);
}
