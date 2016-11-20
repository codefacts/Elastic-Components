package elasta.composer.pipeline.util;

/**
 * Created by Jango on 2016-11-20.
 */
public interface Converters {

    static Integer toInteger(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        return (int) Double.parseDouble(object.toString().trim());
    }

    static Long toLong(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).longValue();
        }
        return (long) Double.parseDouble(object.toString().trim());
    }

    static Float toFloat(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).floatValue();
        }
        return Float.parseFloat(object.toString().trim());
    }

    static Double toDouble(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        return Double.parseDouble(object.toString().trim());
    }

    static Double toNumber(Object object) {
        return toDouble(object);
    }

    static Boolean toBoolean(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Boolean) {
            return (Boolean) object;
        }
        return Boolean.parseBoolean(object.toString().trim().toLowerCase());
    }

    static String toString(Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    public static void main(String[] args) {
        double d = Long.MAX_VALUE;
        System.out.println(d);
        System.out.println(Integer.parseInt("  +00100.00 ".trim()));
    }
}
