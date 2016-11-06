package elasta.core.utils;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by Jango on 11/6/2016.
 */
public class PathTemplateImpl implements PathTemplate {
    private final String template;

    public PathTemplateImpl(String template) {
        this.template = template;
    }

    public MatchAndParams matchAndParams(final String path) {
        return matchAndParams(path, '.');
    }

    public MatchAndParams matchAndParams(final String path, final char pathSeperator) {

        if (template.equals("/")) {
            return new PathTemplate.MatchAndParams(template.equals(path), ImmutableMap.of());
        }

        final String quote = Pattern.quote(String.valueOf(pathSeperator));
        String[] tmpltParts = template.split(quote);
        String[] pathParts = path.split(quote, tmpltParts.length);

        System.out.println(tmpltParts.length + " " + Arrays.toString(tmpltParts));
        System.out.println(pathParts.length + " " + Arrays.toString(pathParts));

        ImmutableMap.Builder<String, String> mapBuilder = ImmutableMap.builder();

        if (tmpltParts.length > pathParts.length) {

            return new MatchAndParams(false, ImmutableMap.of());

        } else if (tmpltParts.length < pathParts.length && !tmpltParts[tmpltParts.length - 1].equals("*")) {

            return new MatchAndParams(false, ImmutableMap.of());
        }

        for (int i = 0; i < tmpltParts.length; i++) {
            final String tmpltPart = tmpltParts[i];
            final String pathPart = pathParts[i];

            if (tmpltPart.equals("*")) {

                return new MatchAndParams(true, mapBuilder.build());

            } else if (tmpltPart.startsWith("{") && tmpltPart.endsWith("}")) {

                mapBuilder.put(tmpltPart.substring(1, tmpltPart.length() - 1), pathPart);

            } else if (!tmpltPart.equals(pathPart)) {

                return new MatchAndParams(false, ImmutableMap.of());
            }
        }

        return new MatchAndParams(true, mapBuilder.build());
    }

    public static void main(String[] args) {
        MatchAndParams matchAndParams = new PathTemplateImpl("abcd").matchAndParams("ab/cd", '.');
        System.out.println(matchAndParams);
    }
}
