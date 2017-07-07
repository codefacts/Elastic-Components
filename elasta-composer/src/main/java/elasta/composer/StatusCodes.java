package elasta.composer;

/**
 * Created by sohan on 5/21/2017.
 */
public interface StatusCodes {
    String success = "success";
    String validationSuccess = "validation.success";
    String authorizationSuccess = "authorization.success";

    String validationError = "validation.error";
    String authorizationError = "authorization.error";
}
