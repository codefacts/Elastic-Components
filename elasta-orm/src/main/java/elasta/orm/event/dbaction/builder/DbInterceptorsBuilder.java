package elasta.orm.event.dbaction.builder;

import elasta.orm.event.dbaction.DbInterceptors;
import elasta.orm.event.dbaction.DeleteDataInterceptor;
import elasta.orm.event.dbaction.TableDataInterceptor;
import elasta.orm.event.dbaction.UpdateTplInterceptor;

import java.util.List;

/**
 * Created by sohan on 4/6/2017.
 */
public interface DbInterceptorsBuilder {

    DbInterceptorsBuilder addTableDataInterceptor(TableDataInterceptor tableDataInterceptor);

    DbInterceptorsBuilder addAllTableDataInterceptors(List<TableDataInterceptor> tableDataInterceptors);

    DbInterceptorsBuilder addDeleteDataInterceptor(DeleteDataInterceptor deleteDataInterceptor);

    DbInterceptorsBuilder addAllDeleteDataInterceptors(List<DeleteDataInterceptor> deleteDataInterceptors);

    DbInterceptorsBuilder addUpdateTplInterceptor(UpdateTplInterceptor updateTplInterceptor);

    DbInterceptorsBuilder addAllUpdateTplInterceptors(List<UpdateTplInterceptor> updateTplInterceptor);

    DbInterceptors build();
}
