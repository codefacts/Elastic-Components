package elasta.commons;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Jango on 9/12/2016.
 */
final public class ReflectionUtils {
    public static List<String> props(Class aClass) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (Field field : aClass.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers())) {
                builder.add(field.getName());
            }
        }
        return builder.build();
    }
}
