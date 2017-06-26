package elasta.orm.table.data.populator;

import elasta.orm.upsert.FieldToColumnMapping;
import elasta.orm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by Jango on 2017-01-10.
 */
final public class TableDataPopulatorImpl implements TableDataPopulator {
    //    private final String table;
//    private final String primaryColumn;
//    private final FieldToColumnMapping[] fieldToColumnMappings;
//    private final List<DirectDependencyDataPopulator> directDependencyDataPopulators;
//    private final List<VirtualDependencyDataPopulator> virtualDependencyDataPopulators;
//
//    public TableDataPopulatorImpl(String table, String primaryColumn, FieldToColumnMapping[] fieldToColumnMappings, List<DirectDependencyDataPopulator> directDependencyDataPopulators, List<VirtualDependencyDataPopulator> virtualDependencyDataPopulators) {
//        Objects.requireNonNull(table);
//        Objects.requireNonNull(primaryColumn);
//        Objects.requireNonNull(fieldToColumnMappings);
//        Objects.requireNonNull(directDependencyDataPopulators);
//        Objects.requireNonNull(virtualDependencyDataPopulators);
//        this.table = table;
//        this.primaryColumn = primaryColumn;
//        this.fieldToColumnMappings = fieldToColumnMappings;
//        this.directDependencyDataPopulators = directDependencyDataPopulators;
//        this.virtualDependencyDataPopulators = virtualDependencyDataPopulators;
//    }
//
//    @Override
    public TableData populate(JsonObject entity
//        , Map<String, JsonObject> entityNameToParentEntityMap
    ) {
//
//        Objects.requireNonNull(entity);
//        Objects.requireNonNull(entityNameToParentEntityMap);
//
//        Map<String, Object> columnToValueMap = new LinkedHashMap<>();
//
//        for (FieldToColumnMapping fieldToColumnMapping : fieldToColumnMappings) {
//
//            final String column = fieldToColumnMapping.getField();
//
//            Object value = entity.getJsonObject(
//                column
//            );
//
//            if (value == null) {
//                continue;
//            }
//
//            columnToValueMap.put(
//                fieldToColumnMapping.getColumn(),
//                value
//            );
//        }
//
//        directDependencyDataPopulators
//            .stream()
//            .filter(directDependencyDataPopulator -> entity.containsKey(directDependencyDataPopulator.column()))
//            .forEach(directDependencyDataPopulator -> {
//
//                    final Map<String, Object> childColumnToValueMap = directDependencyDataPopulator.populateColumnToValueMap(
//                        Optional.ofNullable(
//                            entity.getJsonObject(directDependencyDataPopulator.column())
//                        )
//                    );
//
//                    columnToValueMap.putAll(childColumnToValueMap);
//                }
//            );
//
//        virtualDependencyDataPopulators
//            .stream()
//            .filter(virtualDependencyDataPopulator -> entityNameToParentEntityMap.containsKey(virtualDependencyDataPopulator.parentEntity()))
//            .forEach(virtualDependencyDataPopulator -> {
//
//                Map<String, Object> valueMap = virtualDependencyDataPopulator.populateColumnToValueMap(
//                    Optional.ofNullable(
//                        entityNameToParentEntityMap.get(virtualDependencyDataPopulator.parentEntity())
//                    )
//                );
//
//                columnToValueMap.putAll(valueMap);
//            });
//
//        return new TableData(table, new String[]{primaryColumn}, new JsonObject(columnToValueMap));
        return null;
    }
}
