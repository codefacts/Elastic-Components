package elasta.orm.nm.delete;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.nm.EntityUtils;
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
                    "id", 1025,
                    "designation", ImmutableMap.of(
                        "id", 68765
                    ),
                    "designation2", ImmutableMap.of(
                        "id", 6668968
                    ),
                    "designationList", ImmutableList.of(
                        ImmutableMap.of(
                            "id", 905665
                        ),
                        ImmutableMap.of(
                            "id", 987125
                        )
                    )
                )
            ),
            new DeleteContextImpl(deleteDatas)
        );

        System.out.println(deleteDatas.stream().map(DeleteData::toString).collect(Collectors.joining("\n")));
    }
}
