package elasta.orm.event.dbaction;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.DeleteData;

/**
 * Created by sohan on 4/5/2017.
 */
public interface DeleteDataInterceptor extends DeferredInterceptor<DeleteData> {

    Promise<DeleteData> intercept(DeleteData deleteData);
}
