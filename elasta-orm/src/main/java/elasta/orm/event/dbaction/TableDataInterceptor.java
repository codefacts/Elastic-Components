package elasta.orm.event.dbaction;

import elasta.core.promise.intfs.Promise;
import elasta.orm.upsert.TableData;

/**
 * Created by sohan on 4/5/2017.
 */
public interface TableDataInterceptor extends DeferredInterceptor<TableData> {
    Promise<TableData> intercept(TableData tableData);
}
