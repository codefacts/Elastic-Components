package tracker.model;

/**
 * Created by sohan on 6/30/2017.
 */
public interface UserModel extends BaseModel, elasta.composer.model.request.UserModel {
    String username = "username";
    String email = "email";
    String phone = "phone";
    String password = "password";
}
