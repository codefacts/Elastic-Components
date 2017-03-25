package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.BaseOrm;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by sohan on 3/25/2017.
 */
public interface Delete {
    static void main(String[] asdfasd) {
        JDBCClient jdbcClient = Test.jdbcClient("jpadb", Vertx.vertx(new VertxOptions()
            .setWorkerPoolSize(1)
            .setEventLoopPoolSize(1)
        ));

        BaseOrm baseOrm = Test.baseOrm(Test.Params.builder()
            .entities(Employees.entities())
            .jdbcClient(jdbcClient)
            .build());

        final JsonObject employee = new JsonObject(
            ImmutableMap.of(
                "eid", 6565655,
                "ename", "Sohan",
                "salary", 56565.6565,
                "department", ImmutableMap.of(
                    "id", 656656,
                    "name", "ICT",
                    "department", ImmutableMap.of(
                        "id", 656365,
                        "name", "DCP",
                        "department", ImmutableMap.of(
                            "id", 8721568,
                            "name", "TCP"
                        )
                    )
                ),
                "departments", ImmutableList.of(
                    ImmutableMap.of(
                        "id", 683216789865L,
                        "name", "JCP",
                        "department", ImmutableMap.of(
                            "id", 56982165,
                            "name", "MKV"
                        )
                    )
                )
            )
        );

        baseOrm.delete(
            BaseOrm.DeleteParams.builder()
                .entity("employee")
                .jsonObject(employee)
                .build()
        ).then(jsonObject -> {
            System.out.println("ppp888888888888888888888888888888888888888888888888888888888888888888888");
        }).err(Throwable::printStackTrace);
    }
}
