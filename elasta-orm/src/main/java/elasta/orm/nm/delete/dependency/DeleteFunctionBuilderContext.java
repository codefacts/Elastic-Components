package elasta.orm.nm.delete.dependency;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteFunctionBuilderContext {

    boolean containsTable(String table);

    boolean contains(String table);

    DeleteFunctionBuilderContext putEmpty(String table);

    DeleteFunctionBuilderContext put(String table, DeleteFunction deleteFunction);

    DeleteFunction get(String table);

    DeleteFunctionBuilderContext makeImmutable();

    boolean isEmpty(String dependentTable);
}
