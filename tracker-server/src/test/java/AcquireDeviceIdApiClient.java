import io.reactivex.Observable;

/**
 * Created by sohan on 2017-08-05.
 */
public interface AcquireDeviceIdApiClient {
    Observable<String> acquireNewDeviceId();
}
