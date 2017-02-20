package elasta.orm.nm.upsert;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.impl.Promises;
import elasta.orm.json.sql.DbSql;
import elasta.orm.json.sql.DbSqlImpl;
import elasta.orm.json.sql.SqlBuilderUtilsImpl;
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
import elasta.orm.nm.upsert.builder.impl.UpsertFunctionBuilderImpl;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-21.
 */
public interface UpsertTest {
    public static void main(String[] sdlk) {

        EntityMappingHelper entityMappingHelper = new EntityMappingHelperImpl(EntityUtils.toEntityNameToEntityMap(entities()));

        FunctionMapImpl functionMap = new FunctionMapImpl();

        UpsertFunctionBuilderImpl upsertFunctionGenerator = new UpsertFunctionBuilderImpl(entityMappingHelper, functionMap);

        UpsertFunction upsertFunction = upsertFunctionGenerator.create("employee");

        functionMap.makeImmutable();

        HashMap<String, TableData> map = new LinkedHashMap<>();
        UpsertContextImpl upsertContext = new UpsertContextImpl(map);

        upsertFunction.upsert(
            new JsonObject()
                .put("id", "employee-id-2")
                .put("name", "sohan")
                .put(
                    "designation",
                    new JsonObject()
                        .put("id", "designation-id-1")
                        .put("name", "coder")
                        .put(
                            "employeeList",
                            new JsonArray()
                                .add(
                                    new JsonObject()
                                        .put("id", "employee-id-2")
                                        .put("name", "kony-2")
                                )
                                .add(
                                    new JsonObject()
                                        .put("id", "employee-id-3")
                                        .put("name", "mony-3")
                                )
                        )
                )
                .put(
                    "designation2",
                    new JsonObject()
                        .put("id", "designation-id-2")
                        .put("name", "coder2")
                        .put(
                            "employeeList",
                            new JsonArray()
                                .add(
                                    new JsonObject()
                                        .put("id", "employee-id-1")
                                        .put("name", "sohan")
                                        .put(
                                            "designation",
                                            new JsonObject()
                                                .put("id", "designation-id-1")
                                                .put("name", "coder")
                                                .put(
                                                    "employeeList",
                                                    new JsonArray()
                                                        .add(
                                                            new JsonObject()
                                                                .put("id", "employee-id-2")
                                                                .put("name", "kony-2")
                                                        )
                                                        .add(
                                                            new JsonObject()
                                                                .put("id", "employee-id-3")
                                                                .put("name", "mony-3")
                                                        )
                                                )
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
                                        )
                                )
                        )
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

        map.values().forEach(System.out::println);

        final Vertx vertx = Vertx.vertx();

        final DbSql dbSql = createDbSql("nm", vertx);

        vertx.setTimer(1, event -> {
            Promises.when(map.values().stream().map(tableData -> {
                return dbSql.insertJo(tableData.getTable(), tableData.getValues()).then(aVoid -> System.out.println("complete: " + tableData));
            }).collect(Collectors.toList())).then(voids -> {
                System.out.println("Insert complete");
            }).err(throwable -> throwable.printStackTrace())
            ;
        });
    }

    static DbSql createDbSql(String db, Vertx vertx) {
        final DbSqlImpl dbSql = new DbSqlImpl(
            JDBCClient.createShared(vertx, new JsonObject(
                ImmutableMap.of(
                    "user", "root",
                    "password", "",
                    "driver_class", "com.mysql.jdbc.Driver",
                    "url", "jdbc:mysql://localhost/" + db
                )
            )),
            new SqlBuilderUtilsImpl()
        );
        return dbSql;
    }

    static DbSql createDbSql(String db) {
        return createDbSql(db, Vertx.vertx());
    }

    static Collection<Entity> entities() {

        Entity employee = new Entity(
            "employee",
            "id",
            new Field[]{
                new Field("id", JavaType.STRING, Optional.empty()),
                new Field("name", JavaType.STRING, Optional.empty()),
                new Field("designation", JavaType.OBJECT, Optional.of(
                    new Relationship(Relationship.Type.MANY_TO_ONE, Relationship.Name.HAS_ONE, "designation")
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
                new Field("name", JavaType.STRING),
                new Field("employeeList", JavaType.ARRAY, Optional.of(
                    new Relationship(Relationship.Type.ONE_TO_MANY, Relationship.Name.HAS_MANY, "employee")
                ))
            },
            new DbMapping(
                "designation".toUpperCase(),
                "ID",
                new DbColumnMapping[]{
                    new SimpleColumnMappingImpl("id", "ID", DbType.VARCHAR),
                    new SimpleColumnMappingImpl("name", "NAME", DbType.VARCHAR),
                    new VirtualColumnMappingImpl("EMPLOYEE", "employee", ImmutableList.of(
                        new ForeignColumnMapping(
                            new Column("DESIGNATION_ID", DbType.VARCHAR),
                            new Column("ID", DbType.VARCHAR)
                        )
                    ), "employeeList")
                }
            )
        );

        Entity group = new Entity(
            "group",
            "id",
            new Field[]{
                new Field("id", JavaType.STRING),
                new Field("name", JavaType.STRING),
                new Field("employee", JavaType.OBJECT, Optional.of(
                    new Relationship(
                        Relationship.Type.MANY_TO_ONE,
                        Relationship.Name.HAS_ONE,
                        "employee"
                    )
                ))
            },
            new DbMapping(
                "GROUP",
                "ID",
                new DbColumnMapping[]{
                    new SimpleColumnMappingImpl("id", "ID", DbType.VARCHAR),
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
