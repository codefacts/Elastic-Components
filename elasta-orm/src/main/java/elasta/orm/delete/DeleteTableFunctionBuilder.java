package elasta.orm.delete;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteTableFunctionBuilder {
    DeleteTableFunction build(String table, DeleteTableFunctionBuilderContext context, TableToTableDependenciesMap tableToTableDependenciesMap);
}
