package test;

import com.google.common.collect.ImmutableMap;
import elasta.orm.Orm;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sohan on 5/12/2017.
 */
public interface CountTest {

    static void main(String[] asdfasd) {
        final Orm orm = OrmTest.orm();
        final Random random = new Random();
        List list = new ArrayList();
        for (int i = 0; i < 100; i++) {
            list.add(
                new JsonObject(
                    ImmutableMap.of(
                        "eid", eid(random),
                        "salary", random.nextInt(10)
                    )
                )
            );
        }
        orm.upsertAll(
            "employee", list
        ).err(Throwable::printStackTrace).then(ll -> System.out.println("success"));

    }

    static long eid(Random random) {
        long nextLong = random.nextInt();
        return nextLong < 0 ? -nextLong : nextLong;
    }
}
