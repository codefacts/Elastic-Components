package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.upsert.TableData;

/**
 * Created by Jango on 17/02/16.
 */
public interface PParentDeleteHandler {
    void delete(TableData childEntity, DeleteContext deleteContext);
}
