package elasta.orm.nm.upsert;

import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    Map<String, DbColumnMapping> getColumnNameToColumnMappingMap(String entity);

    Map<String, DbColumnMapping> getFieldToColumnMappingMap(String entity);

    Field getField(String entity, String field);

    DbColumnMapping getColumnMapping(String entity, String field);

    String getPrimaryKey(String entity);

    DbColumnMapping getPrimaryKeyColumnMapping(String entity);

    String getPrimaryKeyColumnName(String entity);
}
