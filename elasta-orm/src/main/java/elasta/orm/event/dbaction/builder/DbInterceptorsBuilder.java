package elasta.orm.event.dbaction.builder;

import elasta.orm.event.dbaction.DbInterceptors;
import elasta.orm.event.dbaction.UpdateTplInterceptor;

import java.util.List;

/**
 * Created by sohan on 4/6/2017.
 */
public interface DbInterceptorsBuilder {

    DbInterceptorsBuilder addUpdateTplInterceptor(UpdateTplInterceptor updateTplInterceptor);

    DbInterceptorsBuilder addAllUpdateTplInterceptors(List<UpdateTplInterceptor> updateTplInterceptor);

    DbInterceptors build();
}
