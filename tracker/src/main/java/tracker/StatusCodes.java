package tracker;

/**
 * Created by sohan on 6/29/2017.
 */
public interface StatusCodes extends elasta.composer.StatusCodes {
    String authenticationError = "authentication.error";
    String userNotFoundError = "user.not.found.error";
    String passwordMismatchError = "password.mismatch.error";
}
