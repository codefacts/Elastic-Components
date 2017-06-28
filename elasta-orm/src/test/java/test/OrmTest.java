package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.BaseOrm;
import elasta.orm.Orm;
import elasta.orm.OrmUtils;
import elasta.orm.impl.OrmImpl;
import elasta.orm.impl.QueryDataLoaderImpl;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.core.JoinType;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by sohan on 5/8/2017.
 */
public interface OrmTest {
    Orm orm = orm();

    static void main(String[] asdfasd) {

        countDistinct();
//        findAll();
//        findAllById();
        findAllByQueryParamsWithNestedArrayFields();
    }

    static void findAllByQueryParamsWithNestedArrayFields() {

        orm.findAll(
            QueryExecutor.QueryParams.builder()
                .entity("employee")
                .alias("e")
                .joinParams(ImmutableList.of(
                    QueryExecutor.JoinParam.builder()
                        .alias("d")
                        .path(PathExpression.parseAndCreate("e.departments"))
                        .build()
                ))
                .selections(
                    ImmutableList.of(
                        new FieldExpressionImpl("e.eid"),
                        new FieldExpressionImpl("e.salary"),
                        new FieldExpressionImpl("e.deg"),
                        new FieldExpressionImpl("e.department.id"),
                        new FieldExpressionImpl("e.department.name"),
                        new FieldExpressionImpl("d.id"),
                        new FieldExpressionImpl("d.name"),
                        new FieldExpressionImpl("d.department.id"),
                        new FieldExpressionImpl("d.department.name")
                    )
                )
                .criteria(OrmUtils.emptyJsonObject())
                .having(OrmUtils.emptyJsonObject())
                .orderBy(ImmutableList.of())
                .groupBy(ImmutableList.of())
                .build()
//            "employee", "e",
//            ImmutableList.of(1201, 5258),

        ).then(jsonObjects -> {
            System.out.println("--------------------RESULT--------------------");
            System.out.println(
                jsonObjects.stream()
                    .map(JsonObject::encodePrettily)
                    .collect(Collectors.joining("\n--------------------------------------------------------------\n"))
            );
        }).err(Throwable::printStackTrace)
        ;
    }

    static void findAllByQueryParams() {

        orm.findAll(
            QueryExecutor.QueryParams.builder()
                .entity("employee")
                .alias("e")
                .joinParams(ImmutableList.of(
                    QueryExecutor.JoinParam.builder()
                        .alias("d")
                        .path(PathExpression.parseAndCreate("e.departments"))
                        .build()
                ))
                .selections(
                    ImmutableList.of(
                        new FieldExpressionImpl("e.eid"),
                        new FieldExpressionImpl("e.salary"),
                        new FieldExpressionImpl("e.deg"),
                        new FieldExpressionImpl("e.department.id"),
                        new FieldExpressionImpl("e.department.name"),
                        new FieldExpressionImpl("d.id"),
                        new FieldExpressionImpl("d.name")
//                        new FieldExpressionImpl("d.department.id"),
//                        new FieldExpressionImpl("d.department.name")
                    )
                )
                .criteria(OrmUtils.emptyJsonObject())
                .having(OrmUtils.emptyJsonObject())
                .orderBy(ImmutableList.of())
                .groupBy(ImmutableList.of())
                .build()
//            "employee", "e",
//            ImmutableList.of(1201, 5258),

        ).then(jsonObjects -> {
            System.out.println("--------------------RESULT--------------------");
            System.out.println(
                jsonObjects.stream()
                    .map(JsonObject::encodePrettily)
                    .collect(Collectors.joining("\n--------------------------------------------------------------\n"))
            );
        }).err(Throwable::printStackTrace)
        ;
    }

    static void findAllById() {
        orm.findAll("employee", "e",
            ImmutableList.of(1201, 5258),
            ImmutableList.of(
                new FieldExpressionImpl("e.eid"),
                new FieldExpressionImpl("e.salary"),
                new FieldExpressionImpl("e.deg"),
                new FieldExpressionImpl("e.department.id"),
                new FieldExpressionImpl("e.department.name")
            )
        ).then(jsonObjects -> {
            System.out.println("--------------------RESULT--------------------");
            System.out.println(
                jsonObjects.stream()
                    .map(JsonObject::encodePrettily)
                    .collect(Collectors.joining("\n--------------------------------------------------------------\n"))
            );
        }).err(Throwable::printStackTrace)
        ;
    }

    static void findAll() {
        orm.findAll("employee", "e", new JsonObject(
                ImmutableMap.of(
                    "op", "gte",
                    "arg1", ImmutableMap.of(
                        "op", "field",
                        "arg", "e.salary"
                    ),
                    "arg2", 8000
                )
            ),
            ImmutableList.of(
                new FieldExpressionImpl("e.eid"),
                new FieldExpressionImpl("e.salary"),
                new FieldExpressionImpl("e.deg"),
                new FieldExpressionImpl("e.department.id"),
                new FieldExpressionImpl("e.department.name")
            )
        ).then(jsonObjects -> {
            System.out.println("--------------------RESULT--------------------");
            System.out.println(
                jsonObjects.stream()
                    .map(JsonObject::encodePrettily)
                    .collect(Collectors.joining("\n--------------------------------------------------------------\n"))
            );
        }).err(Throwable::printStackTrace)
        ;
    }

    static void countDistinct() {
        orm.countDistinct("employee", "e", new JsonObject(

            ImmutableMap.of(
                "op", "gte",
                "arg1", ImmutableMap.of(
                    "op", "field",
                    "arg", "e.salary"
                ),
                "arg2", 8000
            )

        )).then(aLong -> {
            System.out.println("count: " + aLong);
        }).err(Throwable::printStackTrace)
        ;
    }

    static Orm orm() {
        return Test.orm();
    }
}
