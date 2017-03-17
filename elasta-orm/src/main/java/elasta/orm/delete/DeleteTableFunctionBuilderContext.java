package elasta.orm.delete;

/**
 * Created by sohan on 3/11/2017.
 */
public interface DeleteTableFunctionBuilderContext {

    boolean containsTable(String table);

    boolean contains(String table);

    DeleteTableFunctionBuilderContext putEmpty(String table);

    DeleteTableFunctionBuilderContext put(String table, DeleteTableFunction deleteTableFunction);

    DeleteTableFunction get(String table);

    DeleteTableFunctionBuilderContext makeImmutable();

    boolean isEmpty(String dependentTable);
}
