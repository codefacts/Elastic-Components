package elasta.orm.entity;

import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;

import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 2017-01-08.
 */
public interface EntityMappingHelper {

    Entity getEntity(String entity);

    Entity getEntityByTable(String table);

    Field[] getFields(String entity);

    DbMapping getDbMapping(String entity);

    ColumnMapping[] getColumnMappings(String entity);

    Map<String, Field> getFieldNameToFieldMap(String entity);

    Map<String, ColumnMapping> getColumnNameToColumnMappingMap(String entity);

    Map<String, ColumnMapping> getFieldToColumnMappingMap(String entity);

    Field getField(String entity, String field);

    Field getFieldByColumn(String entity, String column);

    ColumnMapping getColumnMapping(String entity, String field);

    RelationMapping getRelationMapping(String entity, String field);

    String getPrimaryKey(String entity);

    ColumnMapping getPrimaryKeyColumnMapping(String entity);

    String getPrimaryKeyColumnName(String entity);

    boolean exists(String entity);

    String getTable(String entity);

    DbMapping getDbMappingByTable(String table);

    List<String> getTables();

    List<Entity> getEntities();
}
