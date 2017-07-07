package tracker.model;

/**
 * Created by sohan on 7/7/2017.
 */
public interface PositionModel extends BaseModel {
    String lat = "lat";
    String lng = "lng";
    String accuracy = "accuracy";
    String time = "time";
    String altitude = "altitude";
    String speed = "speed";
    String provider = "provider";
    String batteryLevel = "batteryLevel";
}
