package elasta.orm.delete;

import elasta.orm.delete.impl.DeleteData;

/**
 * Created by Jango on 2017-01-25.
 */
public interface DeleteContext {
    DeleteContext add(DeleteData deleteData);
}
