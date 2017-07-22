package tracker.model;

/**
 * Created by sohan on 6/30/2017.
 */
public interface UserTable extends BaseTable {
    String user_id = "user_id";
    String username = "username";
    String email = "email";
    String phone = "phone";
    String password = "password";
    String date_of_birth = "date_of_birth";
    String first_name = "first_name";
    String last_name = "last_name";
    String registration_device_type = "reg_dev_type";
    String gender = "gender";
    String picture_uri = "picture_uri";
}
