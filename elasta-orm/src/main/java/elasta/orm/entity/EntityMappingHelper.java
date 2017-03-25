package elasta.orm.entity;

import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.SimpleDbColumnMapping;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    Map<String, SimpleDbColumnMapping> getColumnNameToColumnMappingMap(String entity);

    Map<String, DbColumnMapping> getFieldToColumnMappingMap(String entity);

    Field getField(String entity, String field);

    DbColumnMapping getColumnMapping(String entity, String field);

    String getPrimaryKey(String entity);

    DbColumnMapping getPrimaryKeyColumnMapping(String entity);

    String getPrimaryKeyColumnName(String entity);

    boolean exists(String entity);

    String getTable(String entity);

    DbMapping getDbMappingByTable(String table);

    List<String> getTables();

    List<Entity> getEntities();
}
