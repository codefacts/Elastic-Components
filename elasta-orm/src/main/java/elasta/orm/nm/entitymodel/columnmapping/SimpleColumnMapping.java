package elasta.orm.nm.entitymodel.columnmapping;

import elasta.orm.nm.entitymodel.DbType;

/**
 * Created by Jango on 2017-01-12.
 */
public interface SimpleColumnMapping extends DbColumnMapping {

    String getName();

    DbType getDbType();
}
