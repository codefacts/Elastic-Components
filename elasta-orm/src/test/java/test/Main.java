package test;

import com.google.common.collect.ImmutableList;
import elasta.orm.BaseOrm;
import elasta.orm.query.QueryExecutor;
import elasta.sql.core.JoinType;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Optional;

/**
 * Created by sohan on 3/22/2017.
 */
public interface Main {
    Vertx vertx = Vertx.vertx();

    static void main(String[] asdfasd) {
        JDBCClient jdbcClient = Test.jdbcClient("jpadb", vertx);

        BaseOrm baseOrm = Test.baseOrm(Test.Params.builder()
            .entities(Employees.entities())
            .jdbcClient(jdbcClient)
            .build());

        baseOrm.query(
            QueryExecutor.QueryParams.builder()
                .entity("employee").alias("r")
                .joinParams(ImmutableList.of(
                    QueryExecutor.JoinParam.builder()
                        .path("r.departments")
                        .alias("d")
                        .joinType(Optional.of(
                            JoinType.INNER_JOIN
                        ))
                        .build()
                ))
                .selections(ImmutableList.of(
                    "r.eid",
                    "r.ename",
                    "r.deg",
                    "r.salary",
                    "r.department.id",
                    "r.department.name",
                    "r.department.department.id",
                    "r.department.department.name",
                    "r.department.department.department.id",
                    "r.department.department.department.name",
                    "d.id",
                    "d.name",
                    "d.department.id",
                    "d.department.name"
                ))
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
