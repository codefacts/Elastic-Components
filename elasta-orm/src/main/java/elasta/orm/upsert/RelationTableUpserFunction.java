package elasta.orm.upsert;

/**
 * Created by Jango on 2017-01-10.
 */
public interface RelationTableUpserFunction {
    TableData upsert(TableData srcTableData, TableData dstTableData, UpsertContext upsertContext);
}
