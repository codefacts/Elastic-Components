package elasta.orm.event.dbaction.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.event.dbaction.*;
import elasta.orm.upsert.TableData;
import elasta.sql.core.DeleteData;
import elasta.sql.core.UpdateTpl;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/6/2017.
 */
final public class DbInterceptorsImpl implements DbInterceptors {
    final List<DeleteDataInterceptor> deleteDataInterceptors;
    final List<TableDataInterceptor> tableDataInterceptors;
    final List<UpdateTplInterceptor> updateTplInterceptors;

    public DbInterceptorsImpl(List<DeleteDataInterceptor> deleteDataInterceptors, List<TableDataInterceptor> tableDataInterceptors, List<UpdateTplInterceptor> updateTplInterceptors) {
        Objects.requireNonNull(deleteDataInterceptors);
        Objects.requireNonNull(tableDataInterceptors);
        Objects.requireNonNull(updateTplInterceptors);
        this.deleteDataInterceptors = deleteDataInterceptors;
        this.tableDataInterceptors = tableDataInterceptors;
        this.updateTplInterceptors = updateTplInterceptors;
    }

    @Override
    public Promise<DeleteData> interceptDeleteData(DeleteData deleteData) {
        return dispatch(deleteDataInterceptors, deleteData);
    }

    @Override
    public Promise<TableData> interceptTableData(TableData tableData) {
        return dispatch(tableDataInterceptors, tableData);
    }

    @Override
    public Promise<UpdateTpl> interceptUpdateTpl(UpdateTpl updateTpl) {
        return dispatch(updateTplInterceptors, updateTpl);
    }

    private <T> Promise<T> dispatch(List<? extends DeferredInterceptor<T>> deleteDataInterceptors, T deleteData) {
        if (deleteDataInterceptors.size() <= 0) {
            return Promises.of(deleteData);
        }
        Promise<T> promise = Promises.of(deleteData);

        for (int i = 0; i < deleteDataInterceptors.size(); i++) {
            DeferredInterceptor<T> deleteDataInterceptor = deleteDataInterceptors.get(i);
            promise = promise.mapP(deleteDataInterceptor::intercept);
        }
        return promise;
    }
}
