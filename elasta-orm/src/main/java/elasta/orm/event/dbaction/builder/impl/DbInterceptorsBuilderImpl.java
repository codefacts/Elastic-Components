package elasta.orm.event.dbaction.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.event.dbaction.DbInterceptors;
import elasta.orm.event.dbaction.SqlQueryInterceptor;
import elasta.orm.event.dbaction.UpdateTplInterceptor;
import elasta.orm.event.dbaction.builder.DbInterceptorsBuilder;
import elasta.orm.event.dbaction.impl.DbInterceptorsImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/6/2017.
 */
final public class DbInterceptorsBuilderImpl implements DbInterceptorsBuilder {
    final List<UpdateTplInterceptor> updateTplInterceptors;
    final List<SqlQueryInterceptor> sqlQueryInterceptors;

    public DbInterceptorsBuilderImpl() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public DbInterceptorsBuilderImpl(List<UpdateTplInterceptor> updateTplInterceptors, List<SqlQueryInterceptor> sqlQueryInterceptors) {
        Objects.requireNonNull(updateTplInterceptors);
        Objects.requireNonNull(sqlQueryInterceptors);
        this.updateTplInterceptors = updateTplInterceptors;
        this.sqlQueryInterceptors = sqlQueryInterceptors;
    }

    @Override
    public DbInterceptorsBuilder addUpdateTplInterceptor(UpdateTplInterceptor updateTplInterceptor) {
        this.updateTplInterceptors.add(updateTplInterceptor);
        return this;
    }

    @Override
    public DbInterceptorsBuilder addAllUpdateTplInterceptors(List<UpdateTplInterceptor> updateTplInterceptor) {
        this.updateTplInterceptors.addAll(updateTplInterceptor);
        return this;
    }

    @Override
    public DbInterceptors build() {
        return new DbInterceptorsImpl(
            ImmutableList.copyOf(updateTplInterceptors),
            sqlQueryInterceptors
        );
    }
}
