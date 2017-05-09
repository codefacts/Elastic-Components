package elasta.commons;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Jango on 9/15/2016.
 */
public interface Utils {
    public static boolean not(boolean val) {
        return !val;
    }

    public static <T> T or(T val, T defaultValue) {
        return val != null ? val : defaultValue;
    }

    public static String defaultOnEmptyNullSpace(String val, String defaultValue) {
        return isEmptyNullSpace(val) ? val : defaultValue;
    }

    static boolean isEmptyNullSpace(String val) {
        return (val == null) || val.isEmpty() || val.trim().isEmpty();
    }

    public static <T> T call(Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> immutableCopyNestedMap(Map<String, Object> map) {

        return map;
    }

    public static <T> T cast(Object t) {
        return (T) t;
    }
}
