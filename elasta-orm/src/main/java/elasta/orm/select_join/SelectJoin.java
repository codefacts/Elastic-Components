package elasta.orm.select_join;

import elasta.orm.core.FieldInfo;

import java.util.List;

/**
 * Created by Jango on 10/1/2016.
 */
public interface SelectJoin {
    String toSql(String model, List<FieldInfo> infoList);
}
