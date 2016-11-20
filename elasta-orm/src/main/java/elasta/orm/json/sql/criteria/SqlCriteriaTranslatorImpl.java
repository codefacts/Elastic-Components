package elasta.orm.json.sql.criteria;

import com.google.common.collect.ImmutableList;
import elasta.orm.json.exceptions.SqlParameterException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by Jango on 9/15/2016.
 */
public class SqlCriteriaTranslatorImpl implements SqlCriteriaTranslator {
    private final Map<String, String> functionMap;
    private final Map<String, SqlSymbolTranslator> symbolTranslatorMap;
    private static final String _AND_ = " and ";
    private static final String _OR_ = " or ";
    private static final String NOT_ = "not ";
    private static final String COMMA = ", ";

    public SqlCriteriaTranslatorImpl(Map<String, String> functionMap, Map<String, SqlSymbolTranslator> symbolTranslatorMap) {
        this.functionMap = functionMap;
        this.symbolTranslatorMap = symbolTranslatorMap;
    }

    @Override
    public SqlAndParams toWhereSql(String prefix, JsonObject criteria) {
        StringBuilder stringBuilder = new StringBuilder();
        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();
        new SqlParser(prefix, stringBuilder, paramsBuilder).toSql(criteria);
        return new SqlAndParams(stringBuilder.toString(), new JsonArray(paramsBuilder.build()));
    }

    private class SqlParser {

        final String prefix;
        final StringBuilder stringBuilder;
        final ImmutableList.Builder<Object> paramsBuilder;
        int parameterIndex = 1;

        private SqlParser(String prefix, StringBuilder stringBuilder, ImmutableList.Builder<Object> paramsBuilder) {
            this.prefix = prefix;
            this.stringBuilder = stringBuilder;
            this.paramsBuilder = paramsBuilder;
        }

        private StringBuilder toSql(JsonObject criteria) {

            if (criteria.isEmpty()) return stringBuilder;

            stringBuilder.append("(");

            criteria.forEach(e -> {

                if (e.getKey().equalsIgnoreCase("$or")) {

                    stringBuilder.append("(");

                    if (e.getValue() instanceof JsonArray) {

                        handleOr(((JsonArray) e.getValue()).getList());

                    } else if (e.getValue() instanceof List) {

                        handleOr((List<JsonObject>) e.getValue());

                    } else {

                        throw new SqlParameterException("Invalid value given for key '" + e.getKey() + "'. Value: '" + e.getValue() + "'");
                    }

                    stringBuilder.append(")");

                } else if (e.getKey().equalsIgnoreCase("$not")) {

                    stringBuilder.append(NOT_);

                    if (e.getValue() instanceof JsonObject) {

                        toSql((JsonObject) e.getValue());

                    } else if (e.getValue() instanceof Map) {

                        toSql(new JsonObject((Map<String, Object>) e.getValue()));

                    } else {

                        throw new SqlParameterException("Invalid value given for key '" + e.getKey() + "'. Value: '" + e.getValue() + "'");
                    }

                } else if (e.getValue() instanceof Map) {

                    OperatorValuePair pair = toSqlStr(prefix, e.getKey(), (Map<String, Object>) e.getValue(), stringBuilder);

                    addValue(pair, stringBuilder, paramsBuilder);

                } else if (e.getValue() instanceof JsonObject) {

                    OperatorValuePair pair = toSqlStr(prefix, e.getKey(), ((JsonObject) e.getValue()).getMap(), stringBuilder);

                    addValue(pair, stringBuilder, paramsBuilder);

                } else {

                    stringBuilder.append(prefix + e.getKey() + " = " + nextParameterAlias());
                    paramsBuilder.add(e.getValue());
                }

                stringBuilder.append(_AND_);

            });

            stringBuilder.delete(stringBuilder.length() - _AND_.length(), stringBuilder.length());

            stringBuilder.append(")");

            return stringBuilder;
        }

        private String nextParameterAlias() {
            return "?" + (parameterIndex++);
        }

        private void addValue(OperatorValuePair pair, StringBuilder stringBuilder, ImmutableList.Builder<Object> paramsBuilder) {

            if ((pair.getValue() instanceof Collection) && (((Collection) pair.getValue()).size() > 0)) {

                Collection collection = (Collection) pair.getValue();

                stringBuilder.append(" ").append(pair.getOp()).append(" ").append("(");

                collection.forEach(value -> {
                    stringBuilder.append(nextParameterAlias()).append(COMMA);
                    paramsBuilder.add(value);
                });

                stringBuilder.delete(stringBuilder.length() - COMMA.length(), stringBuilder.length());

                stringBuilder.append(")");

            } else {

                stringBuilder.append(" ").append(pair.getOp()).append(" ").append("?");
                paramsBuilder.add(pair.getValue());
            }
        }

        private void handleOr(List<JsonObject> jsonObjects) {

            if (jsonObjects.isEmpty()) {
                return;
            }

            jsonObjects.forEach(entries -> {

                toSql(entries);
                stringBuilder.append(_OR_);
            });


            stringBuilder.delete(stringBuilder.length() - _OR_.length(), stringBuilder.length());

        }

        private OperatorValuePair toSqlStr(String prefix, String key, Map<String, Object> map, StringBuilder stringBuilder) {
            return map.entrySet().stream().findAny().map(e -> {

                String $key = e.getKey();

                if (functionMap.containsKey($key)) {

                    String function = functionMap.get($key);
                    if (e.getValue() instanceof JsonObject || e.getValue() instanceof Map) {

                        Map<String, Object> js = e.getValue() instanceof JsonObject ? ((JsonObject) e.getValue()).getMap() : (Map<String, Object>) e.getValue();

                        stringBuilder.append(function).append("(");
                        OperatorValuePair valuePair = toSqlStr(prefix, key, js, stringBuilder);
                        stringBuilder.append(")");

                        return valuePair;

                    } else {

                        stringBuilder.append(function).append("(").append(prefix + key).append(")");
                        return new OperatorValuePair("=", e.getValue());
                    }

                } else if (symbolTranslatorMap.containsKey($key)) {

                    SqlSymbolSpec symbolSpec = symbolTranslatorMap.get($key).translate(prefix + key, e.getValue());

                    stringBuilder.append(symbolSpec.getKey());

                    return new OperatorValuePair(symbolSpec.getOperator(), symbolSpec.getValue());
                } else {

                    throw new SqlParameterException("Invalid key given. key: '" + $key + "'");
                }
            }).orElseThrow(() -> new SqlParameterException("Invalid value given key '" + key + "'"));
        }

    }

    public static void main(String[] args) {

        test6();
    }

    private static void test6() {

        String sql = new SqlCriteriaTranslatorImpl(SqlMaps.getFunctionMap(), SqlMaps.getSymbolTranslatorMap())
            .toWhereSql("",
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
            ).getSql();
        System.out.println(sql);
    }

    private static void test5() {

        String sql = new SqlCriteriaTranslatorImpl(SqlMaps.getFunctionMap(), SqlMaps.getSymbolTranslatorMap())
            .toWhereSql("",
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
            ).getSql();
        System.out.println(sql);
    }

    private static void test4() {

        String sql = new SqlCriteriaTranslatorImpl(SqlMaps.getFunctionMap(), SqlMaps.getSymbolTranslatorMap())
            .toWhereSql("",
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
            ).getSql();
        System.out.println(sql);
    }

    private static void test3() {

        String sql = new SqlCriteriaTranslatorImpl(SqlMaps.getFunctionMap(), SqlMaps.getSymbolTranslatorMap())
            .toWhereSql("",
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
            ).getSql();
        System.out.println(sql);
    }

    private static void test2() {
        String sql = new SqlCriteriaTranslatorImpl(SqlMaps.getFunctionMap(), SqlMaps.getSymbolTranslatorMap())
            .toWhereSql("",
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
            ).getSql();
        System.out.println(sql);
    }

    private static void test1() {

        String sql = new SqlCriteriaTranslatorImpl(SqlMaps.getFunctionMap(), SqlMaps.getSymbolTranslatorMap())
            .toWhereSql("",
                new JsonObject()
                    .put("k", "1")
                    .put("k2", "2")
                    .put("k3", 3)
                    .put("k4", 788.9889)
                    .put("k5", true)
                    .put("k9", new Date().toInstant())
                    .put("k10", 90009L)
            ).getSql();
        System.out.println(sql);
    }
}
