package test;

import com.google.common.collect.ImmutableMap;
import elasta.orm.BaseOrm;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by sohan on 3/26/2017.
 */
public interface Delete33 {
    static void main(String[] asdfasd) {

        JDBCClient jdbcClient = Test.jdbcClient("jpadb", Vertx.vertx(new VertxOptions()
            .setWorkerPoolSize(1)
            .setEventLoopPoolSize(1)
        ));

        BaseOrm baseOrm = Test.baseOrm(Test.Params.builder()
            .entities(Employees.entities())
            .jdbcClient(jdbcClient)
            .build());

        JsonObject employee = new JsonObject(
            ImmutableMap.of(
                "eid", 5258
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
