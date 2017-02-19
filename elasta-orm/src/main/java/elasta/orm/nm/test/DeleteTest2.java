package elasta.orm.nm.test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.json.sql.DbSql;
import elasta.orm.nm.EntityUtils;
import elasta.orm.nm.delete.DeleteData;
import elasta.orm.nm.delete.DeleteFunction;
import elasta.orm.nm.delete.builder.impl.DeleteFunctionBuilderImpl;
import elasta.orm.nm.delete.impl.DeleteContextImpl;
import elasta.orm.nm.entitymodel.Entity;
import elasta.orm.nm.entitymodel.impl.EntityMappingHelperImpl;
import elasta.orm.nm.upsert.UpsertTest;
import elasta.orm.nm.upsert.builder.FunctionMapImpl;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * Created by Jango on 17/02/20.
 */
public interface DeleteTest2 {
    static void main(String[] asdf) {
        final Collection<Entity> entities = UpsertTest.entities();
        final EntityMappingHelperImpl helper = new EntityMappingHelperImpl(
            EntityUtils.toEntityNameToEntityMap(entities),
            EntityUtils.toTableToEntityMap(entities)
        );

        final DeleteFunctionBuilderImpl deleteFunctionBuilder = new DeleteFunctionBuilderImpl(helper, new FunctionMapImpl<>(new LinkedHashMap<>()));

        final DeleteFunction deleteFunction = deleteFunctionBuilder.create("employee");

        final DbSql nm = UpsertTest.createDbSql("nm");

        {
            final LinkedHashSet<DeleteData> deleteDataSet = new LinkedHashSet<>();

            deleteFunction.delete(new JsonObject(
                ImmutableMap.of(
                    "id", "employee-id-1",
                    "designation", ImmutableMap.of(
                        "id", "designation-id-2"
                    ),
                    "designation2", ImmutableMap.of(
                        "id", "designation-id-2"
                    ),
                    "designationList", ImmutableList.of(
                        ImmutableMap.of(
                            "id", "designation-id-3"
                        ),
                        ImmutableMap.of(
                            "id", "designation-id-4"
                        ),
                        ImmutableMap.of(
                            "id", "designation-id-5"
                        )
                    )
                )
            ), new DeleteContextImpl(deleteDataSet));

            System.out.println("delete-data-1");
            deleteDataSet.forEach(deleteData -> System.out.println(deleteData));

            nm.delete(deleteDataSet).err(Throwable::printStackTrace)
                .then(signal -> System.out.println("done1"));
        }

        {
            final LinkedHashSet<DeleteData> deleteDataSet2 = new LinkedHashSet<>();

            deleteFunction.delete(new JsonObject(
                ImmutableMap.of(
                    "id", "employee-id-2",
                    "designation", ImmutableMap.of(
                        "id", "designation-id-1"
                    ),
                    "designation2", ImmutableMap.of(
                        "id", "designation-id-2"
                    ),
                    "designationList", ImmutableList.of(
                        ImmutableMap.of(
                            "id", "designation-id-3"
                        ),
                        ImmutableMap.of(
                            "id", "designation-id-4"
                        ),
                        ImmutableMap.of(
                            "id", "designation-id-5"
                        )
                    ),
                    "groupList", ImmutableList.of(
                        ImmutableMap.of(
                            "id", "group-id-3"
                        ),
                        ImmutableMap.of(
                            "id", "group-id-4"
                        ),
                        ImmutableMap.of(
                            "id", "group-id-5"
                        )
                    )
                )
            ), new DeleteContextImpl(deleteDataSet2));

            System.out.println("delete-data-2");
            deleteDataSet2.forEach(deleteData -> System.out.println(deleteData));

            nm.delete(deleteDataSet2).err(Throwable::printStackTrace)
                .then(signal -> System.out.println("done2"));
        }

        {
            final LinkedHashSet<DeleteData> deleteDataSet3 = new LinkedHashSet<>();

            deleteFunction.delete(new JsonObject(
                ImmutableMap.of(
                    "id", "employee-id-3",
                    "designation", ImmutableMap.of(
                        "id", "designation-id-1"
                    )
                )
            ), new DeleteContextImpl(deleteDataSet3));

            System.out.println("delete-data-3");
            deleteDataSet3.forEach(deleteData -> System.out.println(deleteData));

            nm.delete(deleteDataSet3).err(Throwable::printStackTrace)
                .then(signal -> System.out.println("done3"));
        }
    }
}
