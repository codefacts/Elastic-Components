package test;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.impl.Promises;
import elasta.orm.BaseOrm;
import elasta.sql.dbaction.impl.DbInterceptorsImpl;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.core.JoinType;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.stream.Collectors;

/**
 * Created by sohan on 3/22/2017.
 */
public interface Read {

    static void main(String[] asdfasd) {

        BaseOrm baseOrm = Test.baseOrm();

        baseOrm.query(
            QueryExecutor.QueryParams.builder()
                .entity("employee").alias("r")
                .joinParams(ImmutableList.of(
                    QueryExecutor.JoinParam.builder()
                        .path(PathExpression.parseAndCreate("r.departments"))
                        .alias("d")
                        .joinType(JoinType.INNER_JOIN)
                        .build()
                ))
                .selections(ImmutableList.of(
                    "r.eid",
                    "r.ename",
                    "r.deg",
                    "r.salary",
                    "r.department.id",
                    "r.department.name",
                    "r.department.employee.eid",
                    "r.department.employee.ename",
                    "r.department.employee.salary",
                    "r.department.department.id",
                    "r.department.department.name",
                    "r.department.department.employee.ename",
                    "r.department.department.employee.salary",
                    "r.department.department.department.id",
                    "r.department.department.department.name",
                    "r.department.department.department.employee.ename",
                    "r.department.department.department.employee.salary",
                    "r.department2.id",
                    "r.department2.name",
                    "r.department2.employee.eid",
                    "r.department2.employee.ename",
                    "r.department2.department.id",
                    "r.department2.department.name",
                    "r.department2.department.employee.eid",
                    "r.department2.department.employee.ename",
                    "r.department2.department.department.id",
                    "r.department2.department.department.name",
                    "r.department2.department.department.employee.eid",
                    "r.department2.department.department.employee.ename",
                    "r.department2.department.department.employee.salary",
                    "d.id",
                    "d.name",
                    "d.department.id",
                    "d.department.name",
                    "d.department.department.id",
                    "d.department.department.name",
                    "d.department.employee.eid",
                    "d.department.employee.ename",
                    "d.department.employee.departments.id",
                    "d.department.employee.departments.name",
                    "d.department.employee.departments.department.id",
                    "d.department.employee.departments.department.name"
                ).stream().map(FieldExpressionImpl::new).collect(Collectors.toList()))
                .criteria(new JsonObject())
                .orderBy(ImmutableList.of())
                .groupBy(ImmutableList.of())
                .having(new JsonObject())
                .build()
        )
            .err(Throwable::printStackTrace)
            .then(jsonObjects -> {
                System.out.println("size: " + jsonObjects.size());
                jsonObjects.forEach(jsonObject -> {
                    System.out.println(jsonObject.encodePrettily());
                });
            });
    }
}
