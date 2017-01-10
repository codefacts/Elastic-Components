package elasta.orm.nm.entitymodel;

/**
 * Created by Jango on 2017-01-08.
 */
public enum FieldType {
    INTEGER(Integer.class),
    LONG(Long.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    STRING(String.class);

    private final Class aClass;

    FieldType(Class aClass) {
        this.aClass = aClass;
    }

    public Class getType() {
        return aClass;
    }

    public static FieldType of(Class aClass) {
        return aClass == Integer.class ? INTEGER
            : aClass == Long.class ? LONG
            : aClass == Float.class ? FLOAT
            : aClass == Double.class ? DOUBLE
            : aClass == Boolean.class ? BOOLEAN
            : aClass == String.class ? STRING : null;
    }

    @Override
    public String toString() {
        return "FieldType{" +
            "aClass=" + aClass +
            '}';
    }
}