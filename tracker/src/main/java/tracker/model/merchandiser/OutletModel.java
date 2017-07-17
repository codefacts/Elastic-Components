package tracker.model.merchandiser;

import tracker.model.BaseModel;

/**
 * Created by sohan on 7/18/2017.
 */
public interface OutletModel extends BaseModel {
    String name = "name";
    String address = "address";
    String qrCode = "qrCode";
    String location = "location";
    String locationGps = "locationGps";
    String locationNetwork = "locationNetwork";
    String images = "images";
}
