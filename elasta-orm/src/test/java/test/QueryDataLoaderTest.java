package test;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.impl.Promises;
import elasta.orm.BaseOrm;
import elasta.orm.event.dbaction.impl.DbInterceptorsImpl;
import elasta.orm.impl.QueryDataLoaderImpl;
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
 * Created by sohan on 5/1/2017.
 */
public interface QueryDataLoaderTest {
    Vertx vertx = Vertx.vertx(
        new VertxOptions()
            .setEventLoopPoolSize(1)
            .setWorkerPoolSize(1)
            .setInternalBlockingPoolSize(1)
            .setBlockedThreadCheckInterval(10_000_000)
    );

    static void main(String[] asfd) {
        JDBCClient jdbcClient = Test.jdbcClient("jpadb", vertx);

        BaseOrm baseOrm = Test.baseOrm(Test.Params.builder()
            .entities(Employees.entities())
            .jdbcClient(jdbcClient)
            .dbInterceptors(new DbInterceptorsImpl(
                ImmutableList.of(),
                ImmutableList.of(sqlQuery -> {
                    System.out.println(sqlQuery);
                    return Promises.of(sqlQuery);
                })
            ))
            .build());

        QueryDataLoaderImpl queryDataLoader = new QueryDataLoaderImpl(baseOrm, Test.helper(Employees.entities()));

        queryDataLoader.findAll(
            QueryExecutor.QueryParams.builder()
                .entity("employee").alias("r")
                .joinParams(ImmutableList.of())
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
                    "r.department2.department.department.employee.salary"
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
