package elasta.orm.entitymodel.columnmapping;

import elasta.orm.entitymodel.ColumnType;import elasta.orm.entitymodel.ColumnType;

/**
 * Created by Jango on 2017-01-08.
 */
public interface DbColumnMapping {

    String getField();

    ColumnType getColumnType();
}
