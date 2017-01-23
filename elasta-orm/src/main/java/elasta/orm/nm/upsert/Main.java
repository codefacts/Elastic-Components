package elasta.orm.nm.upsert;

import com.google.common.collect.ImmutableList;
import elasta.orm.nm.EntityUtils;
import elasta.orm.nm.entitymodel.*;
import elasta.orm.nm.entitymodel.ForeignColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.DbColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.impl.DirectColumnMappingImpl;
import elasta.orm.nm.entitymodel.columnmapping.impl.IndirectColumnMappingImpl;
import elasta.orm.nm.entitymodel.columnmapping.impl.SimpleColumnMappingImpl;
import elasta.orm.nm.entitymodel.columnmapping.impl.VirtualColumnMappingImpl;
import elasta.orm.nm.entitymodel.impl.EntityMappingHelperImpl;
import elasta.orm.nm.upsert.builder.FunctionMapImpl;
import elasta.orm.nm.upsert.builder.impl.UpsertFunctionGeneratorImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by Jango on 2017-01-21.
 */
public interface Main {
    public static void main(String[] sdlk) {

        EntityMappingHelper entityMappingHelper = new EntityMappingHelperImpl(EntityUtils.toEntityNameToEntityMap(entities()));

        FunctionMapImpl functionMap = new FunctionMapImpl();

        UpsertFunctionGeneratorImpl upsertFunctionGenerator = new UpsertFunctionGeneratorImpl(entityMappingHelper, functionMap);

        UpsertFunction upsertFunction = upsertFunctionGenerator.create("employee");

        functionMap.makeImmutable();

        HashMap<String, TableData> map = new LinkedHashMap<>();
        UpsertContextImpl upsertContext = new UpsertContextImpl(map);

        upsertFunction.upsert(
            new JsonObject()
                .put("id", "employee-id-1")
                .put("name", "sohan")
                .put(
                    "designation",
                    new JsonObject()
                        .put("id", "designation-id-1")
                        .put("name", "coder")
                )
                .put(
                    "designation2",
                    new JsonObject()
                        .put("id", "designation-id-2")
                        .put("name", "coder2")
                )
                .put(
                    "designationList",
                    new JsonArray()
                        .add(
                            new JsonObject()
                                .put("id", "designation-id-3")
                                .put("name", "coder3")
                        )
                        .add(
                            new JsonObject()
                                .put("id", "designation-id-4")
                                .put("name", "coder4")
                        )
                        .add(
                            new JsonObject()
                                .put("id", "designation-id-5")
                                .put("name", "coder5")
                        )
                )
                .put(
                    "groupList",
                    new JsonArray()
                        .add(
                            new JsonObject()
                                .put("id", "group-id-3")
                                .put("name", "group3")
                        )
                        .add(
                            new JsonObject()
                                .put("id", "group-id-4")
                                .put("name", "group4")
                        )
                        .add(
                            new JsonObject()
                                .put("id", "group-id-5")
                                .put("name", "group5")
                        )
                ),
            upsertContext
        );

        map.values().forEach(tableData -> {
            System.out.println(tableData);
        });
    }

    static Collection<Entity> entities() {

        Entity employee = new Entity(
            "employee",
            "id",
            new Field[]{
                new Field("id", JavaType.STRING, Optional.empty()),
                new Field("name", JavaType.STRING, Optional.empty()),
                new Field("designation", JavaType.OBJECT, Optional.of(
                    new Relationship(Relationship.Type.ONE_TO_ONE, Relationship.Name.HAS_ONE, "designation")
                )),
                new Field("designation2", JavaType.OBJECT, Optional.of(
                    new Relationship(Relationship.Type.ONE_TO_ONE, Relationship.Name.HAS_ONE, "designation")
                )),
                new Field("designationList", JavaType.ARRAY, Optional.of(
                    new Relationship(Relationship.Type.ONE_TO_MANY, Relationship.Name.HAS_MANY, "designation")
                )),
                new Field("groupList", JavaType.ARRAY, Optional.of(
                    new Relationship(Relationship.Type.ONE_TO_MANY, Relationship.Name.HAS_MANY, "group")
                ))
            },
            new DbMapping(
                "EMPLOYEE",
                "ID",
                new DbColumnMapping[]{
                    new SimpleColumnMappingImpl("id", "ID", DbType.VARCHAR),
                    new SimpleColumnMappingImpl("name", "NAME", DbType.VARCHAR),
                    new DirectColumnMappingImpl(
                        "designation".toUpperCase(),
                        "designation",
                        ImmutableList.of(
                            new ForeignColumnMapping(new Column("DESIGNATION_ID", DbType.VARCHAR), new Column("ID", DbType.VARCHAR))
                        ),
                        "designation"
                    ),
                    new DirectColumnMappingImpl(
                        "designation".toUpperCase(),
                        "designation",
                        ImmutableList.of(
                            new ForeignColumnMapping(new Column("DESIGNATION2_ID", DbType.VARCHAR), new Column("ID", DbType.VARCHAR))
                        ),
                        "designation2"
                    ),
                    new IndirectColumnMappingImpl(
                        "designationList".toUpperCase(),
                        "designation",
                        "EMPLOY_DESIGNATION",
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                new Column("ID", DbType.VARCHAR),
                                new Column("EMPLOYEE_ID", DbType.VARCHAR)
                            )
                        ),
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                new Column("ID", DbType.VARCHAR),
                                new Column("DESIGNATION_ID", DbType.VARCHAR)
                            )
                        ),
                        "designationList"
                    ),
                    new VirtualColumnMappingImpl(
                        "GROUP",
                        "group",
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                new Column("EMPLOYEE_ID", DbType.VARCHAR),
                                new Column("ID", DbType.VARCHAR)
                            )
                        ),
                        "groupList"
                    )
                }
            )
        );

        Entity designation = new Entity(
            "designation",
            "id",
            new Field[]{
                new Field("id", JavaType.STRING),
                new Field("name", JavaType.STRING)
            },
            new DbMapping(
                "designation".toUpperCase(),
                "ID",
                new DbColumnMapping[]{
                    new SimpleColumnMappingImpl("id", "ID", DbType.VARCHAR),
                    new SimpleColumnMappingImpl("name", "NAME", DbType.VARCHAR)
                }
            )
        );

        Entity group = new Entity(
            "group",
            "id",
            new Field[]{
                new Field("id", JavaType.STRING),
                new Field("name", JavaType.STRING),
                new Field("employee", JavaType.OBJECT)
            },
            new DbMapping(
                "GROUP",
                "ID",
                new DbColumnMapping[]{
                    new SimpleColumnMappingImpl(
                        "id", "ID", DbType.VARCHAR
                    ),
                    new SimpleColumnMappingImpl("name", "NAME", DbType.VARCHAR),
                    new DirectColumnMappingImpl(
                        "EMPLOYEE",
                        "employee",
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                new Column("EMPLOYEE_ID", DbType.VARCHAR),
                                new Column("ID", DbType.VARCHAR)
                            )
                        ),
                        "employee"
                    )
                }
            )
        );

        return Arrays.asList(employee, designation, group);
    }
}
