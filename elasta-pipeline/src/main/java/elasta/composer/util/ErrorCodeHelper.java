package elasta.composer.util;

/**
 * Created by shahadat on 3/31/16.
 */
public final class ErrorCodeHelper {
    static private int validation = 3000_0_0001;
    static private final int validationHttp = 300;

    static private int error = 5000_0_0001;
    static private final int errorHttp = 500;

    public static int validation() {
        return validation++;
    }

    public static int validationHttp() {
        return validationHttp;
    }

    public static int error() {
        return error++;
    }

    public static int errorHttp() {
        return errorHttp;
    }
}
