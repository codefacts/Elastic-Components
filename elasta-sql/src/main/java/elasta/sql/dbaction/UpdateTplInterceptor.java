package elasta.sql.dbaction;

import elasta.core.promise.intfs.Promise;
import elasta.sql.core.UpdateTpl;

/**
 * Created by sohan on 4/6/2017.
 */
public interface UpdateTplInterceptor extends DeferredInterceptor<UpdateTpl> {

    Promise<UpdateTpl> intercept(UpdateTpl updateTpl);
}
