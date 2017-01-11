package elasta.orm.nm.entitymodel.columnmapping;

import elasta.orm.nm.entitymodel.ColumnType;

/**
 * Created by Jango on 2017-01-08.
 */
public interface DbColumnMapping {

    String getField();

    ColumnType getColumnType();
}
