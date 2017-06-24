package elasta.sql.dbaction.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.sql.dbaction.DbInterceptors;
import elasta.sql.dbaction.SqlQueryInterceptor;
import elasta.sql.dbaction.UpdateTplInterceptor;
import elasta.sql.dbaction.builder.DbInterceptorsBuilder;
import elasta.sql.dbaction.impl.DbInterceptorsImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/6/2017.
 */
final public class DbInterceptorsBuilderImpl implements DbInterceptorsBuilder {
    final Collection<UpdateTplInterceptor> updateTplInterceptors;
    final Collection<SqlQueryInterceptor> sqlQueryInterceptors;

    public DbInterceptorsBuilderImpl() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public DbInterceptorsBuilderImpl(Collection<UpdateTplInterceptor> updateTplInterceptors, Collection<SqlQueryInterceptor> sqlQueryInterceptors) {
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
