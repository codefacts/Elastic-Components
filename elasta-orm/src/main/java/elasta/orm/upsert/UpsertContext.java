package elasta.orm.upsert;

/**
 * Created by Jango on 2017-01-09.
 */
public interface UpsertContext {

    UpsertContext put(String tableAndPrimaryKey, TableData tableData);

    UpsertContext putOrMerge(String tableAndPrimaryKey, TableData tableData);

    TableData get(String tableAndPrimaryKey);

    boolean exists(String tableAndPrimaryKey);

}
