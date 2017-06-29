package elasta.composer;

/**
 * Created by sohan on 5/21/2017.
 */
public interface StatusCodes {
    String success = "success";
    String validateSuccess = "validate.success";
    String authorizeSuccess = "authorize.success";

    String validationError = "validate.error";
    String authorizationError = "authorize.error";
}
