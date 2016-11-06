package elasta.core.utils;

import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Jango on 11/6/2016.
 */
public interface PathTemplate {

    MatchAndParams matchAndParams(final String path);

    MatchAndParams matchAndParams(final String path, final char pathSeperator);

    default PathTemplate create(String template) {
        return new PathTemplateImpl(template);
    }

    class MatchAndParams {
        final boolean match;
        final Map<String, String> params;

        public MatchAndParams(boolean match, Map<String, String> params) {
            this.match = match;
            this.params = params;
        }

        public boolean isMatch() {
            return match;
        }

        public Map<String, String> getParams() {
            return params;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MatchAndParams that = (MatchAndParams) o;

            if (match != that.match) return false;
            return params != null ? params.equals(that.params) : that.params == null;

        }

        @Override
        public int hashCode() {
            int result = (match ? 1 : 0);
            result = 31 * result + (params != null ? params.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MatchAndParams{" +
                "match=" + match +
                ", params=" + params +
                '}';
        }
    }
}
