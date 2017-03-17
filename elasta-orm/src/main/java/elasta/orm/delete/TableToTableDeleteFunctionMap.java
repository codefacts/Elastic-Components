package elasta.orm.delete;

/**
 * Created by sohan on 3/12/2017.
 */
public interface TableToTableDeleteFunctionMap {
    DeleteTableFunction get(String table);
}
