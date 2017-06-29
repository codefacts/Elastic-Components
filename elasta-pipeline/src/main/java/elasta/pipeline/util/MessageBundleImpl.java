package elasta.pipeline.util;

import com.google.common.collect.ImmutableMap;
import elasta.pipeline.MessageBundle;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shahadat on 2/28/16.
 */
final public class MessageBundleImpl implements MessageBundle {
    private final Map<String, String> bundle;
    private static final Pattern pattern = Pattern.compile("\\$\\{[a-zA-Z]\\w*?\\}");

    public MessageBundleImpl(String str) {
        this(toMap(new JsonObject(str)));
    }

    private static Map<String, String> toMap(JsonObject jsonObject) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        jsonObject.getMap().forEach((key, value) -> builder.put(key, ((String) value)));

        return builder.build();
    }

    public MessageBundleImpl(Map<String, String> bundle) {
        this.bundle = bundle;
    }

    @Override
    public String translate(String messageCode, JsonObject json) {
        return translateMessage(or(bundle.get(or(messageCode, "")), ""), json);
    }

    private <T> T or(T messageCode, T s) {
        return messageCode == null ? s : messageCode;
    }

    private String translateMessage(String template, JsonObject json) {
        Matcher matcher = pattern.matcher(template);

        final StringBuilder builder = new StringBuilder();
        int start = 0;
        while (matcher.find()) {
            final String key = matcher.group().replace("${", "").replace("}", "");
            builder
                .append(template.substring(start, matcher.start()))
                .append(json.getValue(key, ""))
            ;
            start = matcher.end();
        }

        builder.append(template.substring(start));

        return builder.toString();
    }

    public static void main(String... args) {

    }
}
