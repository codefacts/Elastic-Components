package elasta.orm.entitymodel.columnmapping;

import elasta.orm.entitymodel.DbType;import elasta.orm.entitymodel.DbType;

/**
 * Created by Jango on 2017-01-12.
 */
public interface SimpleDbColumnMapping extends DbColumnMapping {

    String getColumn();

    DbType getDbType();
}
