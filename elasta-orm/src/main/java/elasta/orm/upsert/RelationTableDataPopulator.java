package elasta.orm.upsert;

/**
 * Created by Jango on 2017-01-10.
 */
public interface RelationTableDataPopulator {
    TableData populate(TableData srcTableData, TableData dstTableData);
}
