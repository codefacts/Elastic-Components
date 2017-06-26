package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.BaseOrm;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by sohan on 6/27/2017.
 */
public interface Delete55 {
    static void main(String[] asdfasd) {
        JDBCClient jdbcClient = Test.jdbcClient("jpadb", Vertx.vertx(new VertxOptions()
            .setWorkerPoolSize(1)
            .setEventLoopPoolSize(1)
        ));

        BaseOrm baseOrm = Test.baseOrm(Test.Params.builder()
            .entities(Employees.entities())
            .jdbcClient(jdbcClient)
            .build());

        final JsonObject department = new JsonObject(
            ImmutableMap.of(
                "id", 8721568
            )
        );

        baseOrm.delete(
            BaseOrm.DeleteParams.builder()
                .entity("department")
                .jsonObject(department)
                .build()
        ).thenP(Test.sqlDB(jdbcClient)::update).then(jsonObject -> {
            System.out.println("ppp888888888888888888888888888888888888888888888888888888888888888888888");
        }).err(Throwable::printStackTrace);
    }
}
