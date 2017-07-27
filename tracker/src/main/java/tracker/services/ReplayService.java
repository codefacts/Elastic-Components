package tracker.services;

import io.reactivex.Observable;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 2017-07-26.
 */
public interface ReplayService {
    Observable<JsonObject> next(int count);
}
