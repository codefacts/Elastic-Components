package elasta.orm.json.select_join;

import elasta.orm.json.core.FieldInfo;

import java.util.List;

/**
 * Created by Jango on 10/1/2016.
 */
public interface SelectJoin {
    String toSql(String model, List<FieldInfo> infoList);
}
