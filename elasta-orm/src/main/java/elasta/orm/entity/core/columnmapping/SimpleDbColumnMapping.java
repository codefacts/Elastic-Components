package elasta.orm.entity.core.columnmapping;

import elasta.orm.entity.core.DbType;

/**
 * Created by Jango on 2017-01-12.
 */
public interface SimpleDbColumnMapping extends DbColumnMapping {

    String getColumn();

    DbType getDbType();
}
