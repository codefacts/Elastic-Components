package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.BaseOrm;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

/**
 * Created by sohan on 3/26/2017.
 */
public interface Delete22 {
    static void main(String[] asdfasd) {

        BaseOrm baseOrm = Test.baseOrm();

        JsonObject department = new JsonObject(
            ImmutableMap.of(
                "id", 94504975049L,
                "name", "MCE"
            )
        );

        baseOrm.delete(
            BaseOrm.DeleteParams.builder()
                .entity("department")
                .jsonObject(department)
                .build()
        ).mapP(Test.sqlDB()::update).then(jsonObject -> {
            System.out.println("ppp888888888888888888888888888888888888888888888888888888888888888888888");
        }).err(Throwable::printStackTrace);
    }
}
