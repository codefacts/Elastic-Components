package elasta.orm.delete;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.delete.impl.DeleteContextImpl;import elasta.orm.EntityUtils;
import elasta.orm.delete.builder.impl.DeleteFunctionBuilderImpl;
import elasta.orm.entitymodel.Entity;
import elasta.orm.entitymodel.impl.EntityMappingHelperImpl;
import elasta.orm.upsert.UpsertTest;
import elasta.orm.upsert.builder.FunctionMapImpl;
import elasta.sql.core.DeleteData;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/19.
 */
public interface DeleteTest {
    static void main(String[] asdf) {
        final Collection<Entity> entities = UpsertTest.entities();
        final EntityMappingHelperImpl helper = new EntityMappingHelperImpl(
            EntityUtils.toEntityNameToEntityMap(entities),
            EntityUtils.toTableToEntityMap(entities)
        );

        final DeleteFunctionBuilderImpl deleteFunctionBuilder = new DeleteFunctionBuilderImpl(helper, new FunctionMapImpl<>(new LinkedHashMap<>()));

        final DeleteFunction deleteFunction = deleteFunctionBuilder.create("employee");

        final LinkedHashSet<DeleteData> deleteDatas = new LinkedHashSet<>();

        deleteFunction.delete(
            new JsonObject(
                ImmutableMap.of(
                    "id", "1025",
                    "designation", ImmutableMap.of(
                        "id", "68765",
                        "employeeList", ImmutableList.of(
                            ImmutableMap.of(
                                "id", "865832"
                            )
                        )
                    ),
                    "designation2", ImmutableMap.of(
                        "id", "6668968",
                        "employeeList", ImmutableList.of(
                            ImmutableMap.of(
                                "id", "321326",
                                "groupList", ImmutableList.of(
                                    ImmutableMap.of(
                                        "id", "32565"
                                    )
                                ),
                                "designation", ImmutableMap.of(
                                    "id", "31874"
                                ),
                                "designationList", ImmutableList.of(
                                    ImmutableMap.of(
                                        "id", "265682"
                                    )
                                )
                            )
                        )
                    ),
                    "designationList", ImmutableList.of(
                        ImmutableMap.of(
                            "id", "905665"
                        ),
                        ImmutableMap.of(
                            "id", "987125"
                        )
                    ),
                    "groupList", ImmutableList.of(
                        ImmutableMap.of(
                            "id", "8965"
                        ),
                        ImmutableMap.of(
                            "id", "897656"
                        )
                    )
                )
            ),
            new DeleteContextImpl(deleteDatas)
        );

        System.out.println(deleteDatas.stream().map(DeleteData::toString).collect(Collectors.joining("\n")));
    }
}
