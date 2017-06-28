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

        BaseOrm baseOrm = Test.baseOrm();

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
        ).mapP(Test.sqlDB()::update).then(jsonObject -> {
            System.out.println("ppp888888888888888888888888888888888888888888888888888888888888888888888");
        }).err(Throwable::printStackTrace);
    }
}
