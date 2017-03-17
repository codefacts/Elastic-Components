package elasta.orm.entity.core.columnmapping;

import elasta.orm.entity.core.ColumnType;

/**
 * Created by Jango on 2017-01-08.
 */
public interface DbColumnMapping {

    String getField();

    ColumnType getColumnType();
}
