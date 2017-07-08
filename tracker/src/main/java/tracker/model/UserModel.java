package tracker.model;

/**
 * Created by sohan on 6/30/2017.
 */
public interface UserModel extends BaseModel, elasta.composer.model.request.UserModel {
    String username = "username";
    String password = "password";
    String firstName = "firstName";
    String lastName = "lastName";
    String email = "email";
    String phone = "phone";
    String dateOfBirth = "dateOfBirth";
    String gender = "gender";
    String registrationDeviceType = "registrationDeviceType";
}
