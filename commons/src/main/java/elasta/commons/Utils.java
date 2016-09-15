package elasta.commons;

/**
 * Created by Jango on 9/15/2016.
 */
final public class Utils {
    public static boolean not(boolean val) {
        return !val;
    }

    public static <T> T or(T val, T defaultValue) {
        return val != null ? val : defaultValue;
    }

    public static String defaultOnEmptyNullSpace(String val, String defaultValue) {
        return isEmptyNullSpace(val) ? val : defaultValue;
    }

    private static boolean isEmptyNullSpace(String val) {
        return (val == null) || val.isEmpty() || val.trim().isEmpty();
    }
}
