package elasta.orm.event.dbaction.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.event.dbaction.DbInterceptors;
import elasta.orm.event.dbaction.DeleteDataInterceptor;
import elasta.orm.event.dbaction.TableDataInterceptor;
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
    final List<TableDataInterceptor> tableDataInterceptors;
    final List<DeleteDataInterceptor> deleteDataInterceptors;
    final List<UpdateTplInterceptor> updateTplInterceptors;

    public DbInterceptorsBuilderImpl() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public DbInterceptorsBuilderImpl(List<TableDataInterceptor> tableDataInterceptors, List<DeleteDataInterceptor> deleteDataInterceptors, List<UpdateTplInterceptor> updateTplInterceptors) {
        Objects.requireNonNull(tableDataInterceptors);
        Objects.requireNonNull(deleteDataInterceptors);
        Objects.requireNonNull(updateTplInterceptors);
        this.tableDataInterceptors = tableDataInterceptors;
        this.deleteDataInterceptors = deleteDataInterceptors;
        this.updateTplInterceptors = updateTplInterceptors;
    }

    @Override
    public DbInterceptorsBuilder addTableDataInterceptor(TableDataInterceptor tableDataInterceptor) {
        this.tableDataInterceptors.add(tableDataInterceptor);
        return this;
    }

    @Override
    public DbInterceptorsBuilder addAllTableDataInterceptors(List<TableDataInterceptor> tableDataInterceptors) {
        this.tableDataInterceptors.addAll(tableDataInterceptors);
        return this;
    }

    @Override
    public DbInterceptorsBuilder addDeleteDataInterceptor(DeleteDataInterceptor deleteDataInterceptor) {
        this.deleteDataInterceptors.add(deleteDataInterceptor);
        return this;
    }

    @Override
    public DbInterceptorsBuilder addAllDeleteDataInterceptors(List<DeleteDataInterceptor> deleteDataInterceptors) {
        this.deleteDataInterceptors.addAll(deleteDataInterceptors);
        return this;
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
            ImmutableList.copyOf(deleteDataInterceptors),
            ImmutableList.copyOf(tableDataInterceptors),
            ImmutableList.copyOf(updateTplInterceptors)
        );
    }
}
