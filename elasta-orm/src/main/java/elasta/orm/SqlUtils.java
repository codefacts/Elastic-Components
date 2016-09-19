package elasta.orm;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;

import java.util.Map;

/**
 * Created by Jango on 9/19/2016.
 */
public class SqlUtils {

    public static Map<String, Map<String, JoinSpec>> toTableMap(Map<String, TableSpec> tableSpecMap) {

        ImmutableMap.Builder<String, Map<String, JoinSpec>> mapBuilder = ImmutableMap.builder();

        tableSpecMap.entrySet().forEach(entry -> {

            TableSpec tableSpec = entry.getValue();

            ImmutableMap.Builder<String, JoinSpec> builder = ImmutableMap.builder();
            tableSpec.getColumnSpecs().forEach(columnSpec -> {
                if (columnSpec.getJoinSpec() != null) {
                    builder.put(columnSpec.getColumnName(), columnSpec.getJoinSpec());
                }
            });

            mapBuilder.put(entry.getKey(), builder.build());
        });

        return mapBuilder.build();
    }
}
