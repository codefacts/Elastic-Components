package elasta.orm.select_join;

import elasta.orm.core.FieldInfo;
import elasta.orm.sql.query.SqlQueryGenerator;

import java.util.List;

/**
 * Created by Jango on 10/2/2016.
 */
public class SelectJoinImpl implements SelectJoin {
    private final SqlQueryGenerator queryGenerator;

    public SelectJoinImpl(SqlQueryGenerator queryGenerator) {
        this.queryGenerator = queryGenerator;
    }

    @Override
    public String toSql(String model, List<FieldInfo> infoList) {

        return queryGenerator.toSql(model, infoList);
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
