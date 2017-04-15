package elasta.orm.entity.core.columnmapping;

import elasta.orm.entity.core.DbType;

/**
 * Created by Jango on 2017-01-12.
 */
public interface ColumnMapping {

    String getField();

    String getColumn();

    DbType getDbType();
}
