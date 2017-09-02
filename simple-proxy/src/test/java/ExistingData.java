import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.json.JsonObject;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

/**
 * Created by sohan on 2017-08-22.
 */
public interface ExistingData {
    static void main(String[] args) throws Exception {
        final PrintStream out = new PrintStream(new File(new File("").getAbsoluteFile(), "existing_data.json"));
//        out.println("[");

        final PublishSubject<Map<String, String>> subject = PublishSubject.create();

        long t_1 = System.currentTimeMillis();
        subject
            .skip(1)
            .map(stringStringMap -> new JsonObject(cast_(stringStringMap)))
            .doOnNext(jsonObject -> out.println(jsonObject.encode()))
//            .doOnComplete(() -> out.println("]"))
            .doOnDispose(out::close)
            .subscribe(
                stringStringMap -> {
                },
                Throwable::printStackTrace,
                () -> {
                    long t2 = System.currentTimeMillis();
                    System.out.println("complete time: " + ((t2 - t_1) / 1000) + " sec");
                }
            )
        ;

        ParseCsv.parseFile(
            "C:\\Users\\sohan\\Desktop\\SND 216-17 Outlet list.csv",
            subject
        );
    }

    static Map<String, Object> cast_(Map<String, String> stringStringMap) {
        final Map map = stringStringMap;
        return map;
    }
}
