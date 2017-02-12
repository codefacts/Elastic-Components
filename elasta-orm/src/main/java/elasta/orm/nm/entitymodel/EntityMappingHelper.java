package elasta.orm.nm.entitymodel;

import elasta.orm.nm.entitymodel.columnmapping.DbColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.SimpleColumnMapping;

import java.util.Map;

/**
 * Created by Jango on 2017-01-08.
 */
public interface EntityMappingHelper {

    Entity getEntity(String entity);

    Entity getEntityByTable(String table);

    Field[] getFields(String entity);

    DbMapping getDbMapping(String entity);

    DbColumnMapping[] getColumnMappings(String entity);

    Map<String, Field> getFieldNameToFieldMap(String entity);

    Map<String, SimpleColumnMapping> getColumnNameToColumnMappingMap(String entity);

    Map<String, DbColumnMapping> getFieldToColumnMappingMap(String entity);

    Field getField(String entity, String field);

    DbColumnMapping getColumnMapping(String entity, String field);

    String getPrimaryKey(String entity);

    DbColumnMapping getPrimaryKeyColumnMapping(String entity);

    String getPrimaryKeyColumnName(String entity);

    boolean exists(String entity);

    String getTable(String entity);
}
