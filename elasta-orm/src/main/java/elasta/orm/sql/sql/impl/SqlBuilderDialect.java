package elasta.orm.sql.sql.impl;

/**
 * Created by sohan on 3/14/2017.
 */
public interface SqlBuilderDialect {
    
    String column(String column, String alias);

    String table(String table, String alias);
}
