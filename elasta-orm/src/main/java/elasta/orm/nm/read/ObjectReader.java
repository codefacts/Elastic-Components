package elasta.orm.nm.read;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;

/**
 * Created by Jango on 17/02/12.
 */
public interface ObjectReader {

    JsonObject read(JsonArray data, List<JsonArray> dataList);

    static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        JDBCClient client = JDBCClient.createShared(vertx, new JsonObject(
            ImmutableMap.of(
                "user", "root",
                "password", "",
                "url", "jdbc:mysql://127.0.0.1:3306/jpadb",
                "driver_class", "com.mysql.jdbc.Driver"
            )
        ));

        client.getConnection(event -> {
            if (event.failed()) event.cause().printStackTrace();
            event.result().query("select a.id, a.name, b.id from area a, area b", result -> {
                if (result.failed()) result.cause().printStackTrace();
                List<String> columnNames = result.result().getColumnNames();
                System.out.println(columnNames);
            });
        });
    }
}
