package elasta.orm.nm.delete;

import elasta.orm.nm.delete.impl.DeleteData;

/**
 * Created by Jango on 2017-01-25.
 */
public interface DeleteContext {
    DeleteData add(DeleteData deleteData);
}
