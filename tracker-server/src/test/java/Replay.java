import com.google.common.collect.ImmutableMap;
import elasta.sql.impl.SqlExecutorImpl;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import okhttp3.*;
import tracker.model.PositionModel;
import tracker.server.TrackerServer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohan on 2017-07-23.
 */
public interface Replay {
    Vertx VERTX = Vertx.vertx();
    JDBCClient JDBC_CLIENT = JDBCClient.createShared(VERTX, db());

    static JsonObject db() {
        return new JsonObject(ImmutableMap.of(
            "user", "root",
            "password", "",
            "driver_class", "com.mysql.jdbc.Driver",
            "url", "jdbc:mysql://localhost/tracer_db_backup"
        ));
    }

    OkHttpClient HTTP_CLIENT = new OkHttpClient();

    static void main(String[] asdf) {
        SqlExecutorImpl sqlExecutor = new SqlExecutorImpl(
            JDBC_CLIENT
        );

        PublishSubject<JsonObject> publishSubject = PublishSubject.create();

        publishSubject
            .delay(0, TimeUnit.SECONDS)
//            .filter(positionAndUserId -> positionAndUserId.getFloat(PositionModel.accuracy) < 38)
            .observeOn(Schedulers.from(Executors.newSingleThreadExecutor()))
            .doOnNext(jsonObject -> {

                System.out.println("SENDING: " + jsonObject);

                try {
                    HTTP_CLIENT.newCall(
                        new Request.Builder()
                            .url("http://192.168.0.102:152/api/positions")
                            .headers(Headers.of(ImmutableMap.of(
                                "Authorization", "Bearer 09e19b01-4b9f-4831-a509-6eb1a509ce31"
                            )))
                            .post(
                                RequestBody.create(
                                    MediaType.parse("application/json"),
                                    new JsonObject(ImmutableMap.of(
                                        "lat", jsonObject.getDouble("lat"),
                                        "lng", jsonObject.getDouble("lng"),
                                        "accuracy", jsonObject.getDouble("accuracy")
                                    )).encode()
                                )
                            )
                            .build()
                    ).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }).subscribe(jsonObject -> {
            }, Throwable::printStackTrace,
            () -> System.exit(1));

        sqlExecutor.query("SELECT * FROM `positions` WHERE 1 ORDER BY time ASC")
            .then(resultSet -> {
                List<JsonObject> rows = resultSet.getRows();

                rows.forEach(jsonObject -> {

                    publishSubject.onNext(jsonObject);
                });

                publishSubject.onComplete();

            })
            .err(Throwable::printStackTrace)
        ;

    }
}
