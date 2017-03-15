package elasta.orm.delete;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteFunctionBuilder {
    DeleteFunction build(String table, DeleteFunctionBuilderContext context, TableToTableDependenciesMap tableToTableDependenciesMap);
}
