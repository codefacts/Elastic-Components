package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.impl.Promises;
import elasta.orm.BaseOrm;
import elasta.sql.dbaction.impl.DbInterceptorsImpl;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by sohan on 3/25/2017.
 */
public interface Insert {

    static void main(String[] asdfasd) {
        JDBCClient jdbcClient = Test.jdbcClient("jpadb", Vertx.vertx(new VertxOptions()
            .setWorkerPoolSize(1)
            .setEventLoopPoolSize(1)
        ));

        BaseOrm baseOrm = Test.baseOrm();

        final JsonObject employee = new JsonObject(
            ImmutableMap.<String, Object>builder()
                .put("$isNew", true)
                .putAll(
                    ImmutableMap.of(
                        "eid", 6565655,
                        "ename", "Sohan",
                        "salary", 56565.6565,
                        "department", ImmutableMap.of(
                            "$isNew", true,
                            "id", 656656,
                            "name", "ICT",
                            "department", ImmutableMap.of(
                                "$isNew", true,
                                "id", 656365,
                                "name", "DCP",
                                "department", ImmutableMap.of(
                                    "$isNew", true,
                                    "id", 8721568,
                                    "name", "TCP"
                                )
                            )
                        ),
                        "departments", ImmutableList.of(
                            ImmutableMap.of(
                                "$isNew", true,
                                "id", 683216789865L,
                                "name", "JCP",
                                "department", ImmutableMap.of(
                                    "$isNew", true,
                                    "id", 56982165,
                                    "name", "MKV"
                                )
                            )
                        )
                    )
                )
                .build()
        );

        baseOrm.upsert(
            BaseOrm.UpsertParams.builder()
                .entity("employee")
                .jsonObject(employee)
                .build()
        ).mapP(Test.sqlDB()::update).then(jsonObject -> {
            System.out.println("ppp888888888888888888888888888888888888888888888888888888888888888888888");
        }).err(Throwable::printStackTrace);
    }
}
