package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.orm.BaseOrm;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/26/2017.
 */
public interface Insert22 {
    static void main(String[] asdfasd) {
        JDBCClient jdbcClient = Test.jdbcClient("jpadb", Vertx.vertx(new VertxOptions()
            .setWorkerPoolSize(1)
            .setEventLoopPoolSize(1)
        ));

        BaseOrm baseOrm = Test.baseOrm();

        final JsonObject employee = new JsonObject(
            "{\"eid\":1201,\"ename\":\"Gopal\",\"salary\":40000.0,\"deg\":\"Technical Manager\",\"department\":{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"department2\":{\"id\":988286326887,\"name\":\"BGGV\",\"department\":{\"id\":8283175518,\"name\":\"MKLC\",\"department\":{\"id\":56165582,\"name\":\"VVKM\",\"department\":null,\"employee\":{\"eid\":2389,\"ename\":\"KOMOL\",\"salary\":8000.0,\"deg\":\"DOC\",\"department\":null,\"department2\":null,\"departments\":[]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"departments\":[{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}}]}"
        );

        merge(employee);

        System.out.println(employee.encodePrettily());

        baseOrm.upsert(
            BaseOrm.UpsertParams.builder()
                .entity("employee")
                .jsonObject(employee)
                .build()
        ).mapP(Test.sqlDB()::update).then(jsonObject -> {
            System.out.println("ppp888888888888888888888888888888888888888888888888888888888888888888888");
        }).err(Throwable::printStackTrace);
    }

    static void merge(JsonObject employee) {

//        ImmutableSet<Long> numbers = ImmutableSet.of(267935328L, 5258L, 6538921L, 94504975049L, 98457984L, 98798079087L, 2389L);
        ImmutableSet<Long> numbers = ImmutableSet.of();

        if (!numbers.contains(employee.getLong("eid")) && !numbers.contains(employee.getLong("id"))) {
            employee.put("$isNew", true);
        }
        employee.getMap().forEach((key, value) -> {
            if (value instanceof Map) {
                merge(new JsonObject(((Map) value)));
            } else if (value instanceof List) {
                ((List) value).forEach(o -> {
                    if (o instanceof Map) {
                        merge(new JsonObject(((Map) o)));
                    }
                });
            }
        });
    }
}
