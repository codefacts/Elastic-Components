package elasta.orm.query;

import com.google.common.collect.ImmutableList;
import elasta.orm.EntityUtils;
import elasta.orm.entitymodel.Entity;
import elasta.orm.entitymodel.impl.EntityMappingHelperImpl;
import elasta.orm.query.builder.QueryBuilder;
import elasta.orm.query.builder.impl.QueryBuilderImpl;
import elasta.orm.query.impl.PathExpressionImpl;
import elasta.orm.upsert.UpsertTest;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Jango on 17/02/18.
 */
public interface QueryTest {
    static void main(String[] arasd) {
        final Collection<Entity> entities = UpsertTest.entities();
        final EntityMappingHelperImpl helper = new EntityMappingHelperImpl(
            EntityUtils.toEntityNameToEntityMap(entities),
            EntityUtils.toTableToEntityMap(entities)
        );

        final QueryBuilder q = new QueryBuilderImpl(helper);

        q.fromBuilder().root("employee", "r");
        q.fromBuilder().join(new PathExpressionImpl("r.groupList"), "g", Optional.empty());
        q.fromBuilder().join(new PathExpressionImpl("r.designationList"), "d", Optional.empty());

        q.selectBuilder().add(
            ImmutableList.of(
                q.select("r.id"),
                q.select("r.name"),
                q.select("d.name"),
                q.select("r.designation.id"),
                q.select("r.designation.name"),
                q.select("r.designation2.id"),
                q.select("r.designation2.name"),
                q.select("r.designation.id"),
                q.select("r.designation.name"),
                q.select("g.id"),
                q.select("g.employee.name"),
                q.select("g.employee.designation.name"),
                q.select("g.employee.designation2.name"),
                q.select("g.employee.designation.name"),
                q.select("g.employee.designation2.name"),
                q.select("g.employee.designation.name")
            )
        );

        final String sql = q.build().toSql();
        System.out.println(sql);
    }
}
