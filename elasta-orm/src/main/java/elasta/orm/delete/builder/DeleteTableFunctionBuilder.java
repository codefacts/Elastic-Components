package elasta.orm.delete.builder;

import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.TableToTableDependenciesMap;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteTableFunctionBuilder {
    DeleteTableFunction build(String table, DeleteTableFunctionBuilderContext context, TableToTableDependenciesMap tableToTableDependenciesMap);
}
