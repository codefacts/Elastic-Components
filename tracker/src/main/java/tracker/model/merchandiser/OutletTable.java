package tracker.model.merchandiser;

import tracker.model.BaseTable;

/**
 * Created by sohan on 7/18/2017.
 */
public interface OutletTable extends BaseTable {
    String name = "name";
    String address = "address";
    String qr_code = "qr_code";
    String location_id = "location_id";
    String location_id_gps = "location_id_gps";
    String location_id_network = "location_id_network";
    String images = "images";
}
