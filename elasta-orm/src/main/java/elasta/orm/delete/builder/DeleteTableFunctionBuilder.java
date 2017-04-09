package elasta.orm.delete.builder;

import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.TableToTableDependenciesMap;
import elasta.orm.event.builder.BuilderContext;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteTableFunctionBuilder {
    DeleteTableFunction build(String table, BuilderContext<DeleteTableFunction> context, TableToTableDependenciesMap tableToTableDependenciesMap);
}
