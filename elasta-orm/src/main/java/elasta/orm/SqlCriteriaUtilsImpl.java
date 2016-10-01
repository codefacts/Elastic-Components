package elasta.orm;

import elasta.orm.exceptions.SqlParameterException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 9/15/2016.
 */
public class SqlCriteriaUtilsImpl implements SqlCriteriaUtils {
    private final Map<String, String> functionMap;
    private final Map<String, SqlSymbolTranslator> symbolTranslatorMap;
    private static final String _AND_ = " and ";
    private static final String _OR_ = " or ";
    private static final String NOT_ = "not ";

    public SqlCriteriaUtilsImpl(Map<String, String> functionMap, Map<String, SqlSymbolTranslator> symbolTranslatorMap) {
        this.functionMap = functionMap;
        this.symbolTranslatorMap = symbolTranslatorMap;
    }

    @Override
    public String toSql(JsonObject criteria) {

        return toSql(criteria, new StringBuilder()).toString();
    }

    private StringBuilder toSql(JsonObject criteria, StringBuilder stringBuilder) {

        if (criteria.isEmpty()) return stringBuilder;

        stringBuilder.append("(");

        criteria.forEach(e -> {

            if (e.getKey().equalsIgnoreCase("$or")) {

                stringBuilder.append("(");

                if (e.getValue() instanceof JsonArray) {

                    handleOr(((JsonArray) e.getValue()).getList(), stringBuilder);

                } else if (e.getValue() instanceof List) {

                    handleOr((List<JsonObject>) e.getValue(), stringBuilder);

                } else {

                    throw new SqlParameterException("Invalid value given for key '" + e.getKey() + "'. Value: '" + e.getValue() + "'");
                }

                stringBuilder.append(")");

            } else if (e.getKey().equalsIgnoreCase("$not")) {

                stringBuilder.append(NOT_);

                if (e.getValue() instanceof JsonObject) {

                    toSql((JsonObject) e.getValue(), stringBuilder);

                } else if (e.getValue() instanceof Map) {

                    toSql(new JsonObject((Map<String, Object>) e.getValue()), stringBuilder);

                } else {

                    throw new SqlParameterException("Invalid value given for key '" + e.getKey() + "'. Value: '" + e.getValue() + "'");
                }

            } else if (e.getValue() instanceof Map) {

                OperatorValuePair pair = toSqlStr(e.getKey(), (Map<String, Object>) e.getValue(), stringBuilder);
                stringBuilder.append(" ").append(pair.getOp()).append(" ").append(pair.getValue());

            } else if (e.getValue() instanceof JsonObject) {

                OperatorValuePair pair = toSqlStr(e.getKey(), ((JsonObject) e.getValue()).getMap(), stringBuilder);
                stringBuilder.append(" ").append(pair.getOp()).append(" ").append(pair.getValue());

            } else {

                stringBuilder.append(e.getKey() + " = " + toSqlValue(e.getValue()));
            }

            stringBuilder.append(_AND_);

        });

        stringBuilder.delete(stringBuilder.length() - _AND_.length(), stringBuilder.length());

        stringBuilder.append(")");

        return stringBuilder;
    }

    private void handleOr(List<JsonObject> jsonObjects, StringBuilder stringBuilder) {

        if (jsonObjects.isEmpty()) {
            return;
        }

        jsonObjects.forEach(entries -> {

            toSql(entries, stringBuilder);
            stringBuilder.append(_OR_);
        });


        stringBuilder.delete(stringBuilder.length() - _OR_.length(), stringBuilder.length());

    }

    private OperatorValuePair toSqlStr(String key, Map<String, Object> map, StringBuilder stringBuilder) {
        return map.entrySet().stream().findAny().map(e -> {

            String $key = e.getKey();

            if (functionMap.containsKey($key)) {

                String function = functionMap.get($key);
                if (e.getValue() instanceof JsonObject || e.getValue() instanceof Map) {

                    Map<String, Object> js = e.getValue() instanceof JsonObject ? ((JsonObject) e.getValue()).getMap() : (Map<String, Object>) e.getValue();

                    stringBuilder.append(function).append("(");
                    OperatorValuePair valuePair = toSqlStr(key, js, stringBuilder);
                    stringBuilder.append(")");

                    return valuePair;

                } else {

                    stringBuilder.append(function).append("(").append(key).append(")");
                    return new OperatorValuePair("=", toSqlValue(e.getValue()));
                }

            } else if (symbolTranslatorMap.containsKey($key)) {

                SqlSymbolSpec symbolSpec = symbolTranslatorMap.get($key).translate(key, e.getValue());

                stringBuilder.append(symbolSpec.getKey());

                return new OperatorValuePair(symbolSpec.getOperator(), symbolSpec.getValue());
            } else {

                throw new SqlParameterException("Invalid key given. key: '" + $key + "'");
            }
        }).orElseThrow(() -> new SqlParameterException("Invalid value given key '" + key + "'"));
    }

    private String toSqlValue(Object value) {
        return (value.getClass() == String.class ? ("'" + value + "'") : value.toString());
    }

    public static void main(String[] args) {

        test6();
    }

    private static void test6() {

        String sql = new SqlCriteriaUtilsImpl(SqlInfo.getFunctionMap(), SqlInfo.getSymbolTranslatorMap())
            .toSql(
                new JsonObject()
                    .put("k", "1")
                    .put("k3", 3)
                    .put("$or",
                        new JsonArray()
                            .add(new JsonObject()
                                .put("kk1", new JsonObject().put(CriteriaCns.NIN.toString(), new JsonArray().add("434").add(4534).add(true)))
                                .put("$not",
                                    new JsonObject()
                                        .put("kk2", new JsonObject().put(CriteriaCns.NLK.toString(), "romo"))
                                        .put("$or",
                                            new JsonArray()
                                                .add(
                                                    new JsonObject()
                                                        .put("kk3", new JsonObject().put(CriteriaCns.CNTNS.toString(), "me"))
                                                        .put("lg",
                                                            new JsonObject().put("$eq", 15)
                                                        )
                                                )
                                                .add(
                                                    new JsonObject()
                                                        .put("kk4",
                                                            new JsonObject()
                                                                .put(SqlFunctions.UPPER.toString(),
                                                                    new JsonObject()
                                                                        .put(SqlFunctions.LENGTH.toString(),
                                                                            new JsonObject()
                                                                                .put(SqlFunctions.CIEL.toString(),
                                                                                    new JsonObject().put(CriteriaCns.IN.toString(),
                                                                                        new JsonArray().add(843).add("ksl").add(true).add(934L)
                                                                                    )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                                .add(new JsonObject().put("lsv", new JsonObject().put("$eqi", "sohan")))
                                        )
                                )
                            )
                            .add(new JsonObject()
                                .put("gs", new JsonObject().put("$ne", "kona"))
                                .put("lk", new JsonObject().put(CriteriaCns.ENDW.toString(), "moni"))
                            )
                    )
                    .put("k10", 90009L)
            );
        System.out.println(sql);
    }

    private static void test5() {

        String sql = new SqlCriteriaUtilsImpl(SqlInfo.getFunctionMap(), SqlInfo.getSymbolTranslatorMap())
            .toSql(
                new JsonObject()
                    .put("k", "1")
                    .put("k3", 3)
                    .put("$or",
                        new JsonArray()
                            .add(new JsonObject()
                                .put("kk1", new JsonObject().put(CriteriaCns.NIN.toString(), new JsonArray().add("434").add(4534).add(true)))
                                .put("$not",
                                    new JsonObject()
                                        .put("kk2", new JsonObject().put(CriteriaCns.NLK.toString(), "romo"))
                                        .put("$or",
                                            new JsonArray()
                                                .add(
                                                    new JsonObject()
                                                        .put("kk3", new JsonObject().put(CriteriaCns.CNTNS.toString(), "me"))
                                                        .put("lg",
                                                            new JsonObject().put("$eq", 15)
                                                        )
                                                )
                                                .add(
                                                    new JsonObject()
                                                        .put("kk4",
                                                            new JsonObject()
                                                                .put(SqlFunctions.UPPER.toString(),
                                                                    new JsonObject()
                                                                        .put(SqlFunctions.LENGTH.toString(),
                                                                            new JsonObject()
                                                                                .put(SqlFunctions.CIEL.toString(),
                                                                                    new JsonObject().put(CriteriaCns.NIN.toString(),
                                                                                        new JsonArray().add(843).add("ksl").add(true).add(934L)
                                                                                    )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                                .add(new JsonObject().put("lsv", new JsonObject().put("$eqi", "sohan")))
                                        )
                                )
                            )
                            .add(new JsonObject()
                                .put("gs", new JsonObject().put("$ne", "kona"))
                                .put("lk", new JsonObject().put(CriteriaCns.ENDW.toString(), "moni"))
                            )
                    )
                    .put("k10", 90009L)
            );
        System.out.println(sql);
    }

    private static void test4() {

        String sql = new SqlCriteriaUtilsImpl(SqlInfo.getFunctionMap(), SqlInfo.getSymbolTranslatorMap())
            .toSql(
                new JsonObject()
                    .put("k", "1")
                    .put("k3", 3)
                    .put("$or",
                        new JsonArray()
                            .add(new JsonObject()
                                .put("kk1", new JsonObject().put(CriteriaCns.NIN.toString(), new JsonArray().add("434").add(4534).add(true)))
                                .put("$not",
                                    new JsonObject()
                                        .put("kk2", new JsonObject().put(CriteriaCns.NLK.toString(), "romo"))
                                        .put("$or",
                                            new JsonArray()
                                                .add(
                                                    new JsonObject()
                                                        .put("kk3", new JsonObject().put(CriteriaCns.CNTNS.toString(), "me"))
                                                        .put("lg",
                                                            new JsonObject().put("$eq", 15)
                                                        )
                                                )
                                                .add(
                                                    new JsonObject()
                                                        .put("kk4", new JsonObject().put("$lte", 85))
                                                )
                                                .add(new JsonObject().put("lsv", new JsonObject().put("$eqi", "sohan")))
                                        )
                                )
                            )
                            .add(new JsonObject()
                                .put("gs", new JsonObject().put("$ne", "kona"))
                                .put("lk", new JsonObject().put(CriteriaCns.ENDW.toString(), "moni"))
                            )
                    )
                    .put("k10", 90009L)
            );
        System.out.println(sql);
    }

    private static void test3() {

        String sql = new SqlCriteriaUtilsImpl(SqlInfo.getFunctionMap(), SqlInfo.getSymbolTranslatorMap())
            .toSql(
                new JsonObject()
                    .put("k", "1")
                    .put("k3", 3)
                    .put("$or",
                        new JsonArray()
                            .add(new JsonObject()
                                .put("kk1", 55)
                                .put("$not",
                                    new JsonObject()
                                        .put("kk2", 873493L)
                                        .put("$or",
                                            new JsonArray()
                                                .add(
                                                    new JsonObject()
                                                        .put("kk3", true)
                                                        .put("lg",
                                                            new JsonObject().put("$eq", 15)
                                                        )
                                                )
                                                .add(
                                                    new JsonObject()
                                                        .put("kk4", new JsonObject().put("$lte", 85))
                                                )
                                                .add(new JsonObject().put("lsv", new JsonObject().put("$eqi", "sohan")))
                                        )
                                )
                            )
                            .add(new JsonObject()
                                .put("gs", "ss")
                                .put("lk", true)
                            )
                    )
                    .put("k10", 90009L)
            );
        System.out.println(sql);
    }

    private static void test2() {
        String sql = new SqlCriteriaUtilsImpl(SqlInfo.getFunctionMap(), SqlInfo.getSymbolTranslatorMap())
            .toSql(
                new JsonObject()
                    .put("k", "1")
                    .put("k2", "2")
                    .put("k3", 3)
                    .put("$or",
                        new JsonArray()
                            .add(new JsonObject().put("kk", 55).put("ty", "osp"))
                            .add(new JsonObject().put("gs", "ss").put("lk", true)))
                    .put("k4", 788.9889)
                    .put("k5", true)
                    .put("k9", new Date().toInstant())
                    .put("k10", 90009L)
            );
        System.out.println(sql);
    }

    private static void test1() {

        String sql = new SqlCriteriaUtilsImpl(SqlInfo.getFunctionMap(), SqlInfo.getSymbolTranslatorMap())
            .toSql(
                new JsonObject()
                    .put("k", "1")
                    .put("k2", "2")
                    .put("k3", 3)
                    .put("k4", 788.9889)
                    .put("k5", true)
                    .put("k9", new Date().toInstant())
                    .put("k10", 90009L)
            );
        System.out.println(sql);
    }
}
