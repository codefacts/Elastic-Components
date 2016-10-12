package elasta.orm.json.sql.criteria;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import static elasta.orm.json.sql.criteria.SqlCriteriaHelper.*;

/**
 * Created by Jango on 9/16/2016.
 */
public class SqlMaps {
    private static final Map<String, SqlSymbolTranslator> SYMBOL_TRANSLATOR_MAP;
    private static final Map<String, String> FUNCTION_MAP;

    public static Map<String, SqlSymbolTranslator> getSymbolTranslatorMap() {
        return SYMBOL_TRANSLATOR_MAP;
    }

    public static Map<String, String> getFunctionMap() {
        return FUNCTION_MAP;
    }

    static {

        ImmutableMap.Builder<String, SqlSymbolTranslator> builder = ImmutableMap.builder();

        builder
            .put(CriteriaCns.CNTNS.toString(), (key, value) -> new SqlSymbolSpec(key, "like", toValueStr("%" + value + "%")))
            .put(CriteriaCns.ENDW.toString(), (key, value) -> new SqlSymbolSpec(key, "like", toValueStr("%" + value)))
            .put(CriteriaCns.STW.toString(), (key, value) -> new SqlSymbolSpec(key, "like", toValueStr(value + "%")))
            .put(CriteriaCns.LK.toString(), (key, value) -> new SqlSymbolSpec(key, "like", toValueStr(value)))
            .put(CriteriaCns.NLK.toString(), (key, value) -> new SqlSymbolSpec(key, "not like", toValueStr(value)))
            .put(CriteriaCns.EQI.toString(), (key, value) -> new SqlSymbolSpec("UPPER(" + key + ")", "=", toValueStr(value).toUpperCase()))
            .put(CriteriaCns.IN.toString(), (key, value) -> new SqlSymbolSpec(key, "in", SqlCriteriaHelper.toListSql(value)))
            .put(CriteriaCns.NIN.toString(), (key, value) -> new SqlSymbolSpec(key, "not in", SqlCriteriaHelper.toListSql(value)))
            .put(CriteriaCns.LT.toString(), (key, value) -> new SqlSymbolSpec(key, "<", toValueStr(value)))
            .put(CriteriaCns.LTE.toString(), (key, value) -> new SqlSymbolSpec(key, "<=", toValueStr(value)))
            .put(CriteriaCns.GT.toString(), (key, value) -> new SqlSymbolSpec(key, ">", toValueStr(value)))
            .put(CriteriaCns.GTE.toString(), (key, value) -> new SqlSymbolSpec(key, ">=", toValueStr(value)))
            .put(CriteriaCns.EQ.toString(), (key, value) -> new SqlSymbolSpec(key, "=", toValueStr(value)))
            .put(CriteriaCns.NE.toString(), (key, value) -> new SqlSymbolSpec(key, "!=", toValueStr(value)))
        ;

        SYMBOL_TRANSLATOR_MAP = builder.build();
    }

    static {

        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        builder
            .put(SqlFunctions.UPPER.toString(), "upper")
            .put(SqlFunctions.LOWER.toString(), "lower")
            .put(SqlFunctions.LENGTH.toString(), "length")
        ;
        builder.put(SqlFunctions.CIEL.toString(), "ceil")
            .put(SqlFunctions.FLOOR.toString(), "floor")
            .put(SqlFunctions.ABS.toString(), "abs")
        ;

        FUNCTION_MAP = builder.build();
    }
}
