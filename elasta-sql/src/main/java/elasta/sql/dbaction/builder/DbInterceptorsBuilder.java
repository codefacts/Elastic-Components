package elasta.sql.dbaction.builder;

import elasta.sql.dbaction.DbInterceptors;
import elasta.sql.dbaction.UpdateTplInterceptor;

import java.util.List;

/**
 * Created by sohan on 4/6/2017.
 */
public interface DbInterceptorsBuilder {

    DbInterceptorsBuilder addUpdateTplInterceptor(UpdateTplInterceptor updateTplInterceptor);

    DbInterceptorsBuilder addAllUpdateTplInterceptors(List<UpdateTplInterceptor> updateTplInterceptor);

    DbInterceptors build();
}
