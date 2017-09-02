import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.json.JsonObject;
import okhttp3.*;
import tracker.server.ex.RequestException;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by sohan on 2017-08-05.
 */
final public class AcquireDeviceIdApiClientImpl implements AcquireDeviceIdApiClient {
    static final String TAG = AcquireDeviceIdApiClientImpl.class.getSimpleName();
    final String baseUri;
    final OkHttpClient httpClient;
    final Scheduler scheduler;

    public AcquireDeviceIdApiClientImpl(String baseUri, OkHttpClient httpClient, Scheduler scheduler) {
        Objects.requireNonNull(baseUri);
        Objects.requireNonNull(httpClient);
        Objects.requireNonNull(scheduler);
        this.baseUri = baseUri;
        this.httpClient = httpClient;
        this.scheduler = scheduler;
    }

    @Override
    public Observable<String> acquireNewDeviceId() {

        return Observable.fromCallable(() -> {

            long start = System.currentTimeMillis();

            HashMap<String, Object> map = new HashMap<>();

            map.put("androidDeviceToken", "kdheofdsys;fhrvtwo38rpcmbgbhdiig-b7wngy9gir993,vh9dte-46to3nf8gyd");

            String uri = baseUri + "/api/devices";

            final Response response = httpClient.newCall(
                new Request.Builder()
                    .url(uri)
                    .post(RequestBody.create(
                        MediaType.parse("application/json; charset=utf-8"), new JsonObject(map).encode())
                    )
                    .build()
            ).execute();

            try {

                checkApiSuccessOrThrow(response);

                String deviceId = new JsonObject(response.body().string()).getString("deviceId");

                Objects.requireNonNull(deviceId);

                return deviceId;

            } finally {
                response.close();
                long end = System.currentTimeMillis();
                System.out.println("time: " + (end - start) / 1000);
            }

        }).subscribeOn(scheduler);
    }

    public static void checkApiSuccessOrThrow(Response response) throws Exception {

        if (response.isSuccessful()) {
            return;
        }

        final ResponseBody body = response.body();

        if (400 <= response.code() && response.code() < 500 && body != null) {

            JsonObject jsonObject = new JsonObject(body.string());

            throw new RequestException(
                "Error", "Orror"
            );
        }

        throw new RequestException("", response.message());
    }

    public static void main(String[] asfd) throws InterruptedException {
        new AcquireDeviceIdApiClientImpl(
            "http://192.168.0.102:8080",
            new OkHttpClient(),
            Schedulers.io()
        ).acquireNewDeviceId()
        .subscribe(s -> {
            System.out.println("deviceId: " + s);
        }, Throwable::printStackTrace);

        Thread.sleep(100000);
    }
}
