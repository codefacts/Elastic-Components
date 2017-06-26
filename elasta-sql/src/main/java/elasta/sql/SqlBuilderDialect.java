package elasta.sql;

/**
 * Created by sohan on 3/14/2017.
 */
public interface SqlBuilderDialect {

    String column(String column, String alias);

    String column(String column);

    String table(String table, String alias);

    String table(String table);

    String nullValue();
}
