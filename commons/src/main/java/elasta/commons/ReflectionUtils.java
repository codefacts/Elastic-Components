package elasta.commons;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Jango on 9/12/2016.
 */
final public class ReflectionUtils {
    public static List<String> staticFinalFieldValues(Class aClass) {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (Field field : aClass.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers())
                && Modifier.isStatic(field.getModifiers())
                && Modifier.isFinal(field.getModifiers())) {
                try {
                    builder.add(String.valueOf(field.get(null)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.build();
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
