package elasta.orm.sql;

import com.google.common.collect.ImmutableList;
import elasta.orm.sql.core.*;

import java.util.List;

/**
 * Created by Jango on 9/24/2016.
 */
public class TestCases {
    public static List<TableSpec> createList() {

        ImmutableList.Builder<TableSpec> builder = ImmutableList.builder();
        builder
            .add(
                new TableSpecBuilder()
                    .setTableName("users")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setColumnType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("user_id")
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("name")
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("email")
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("phone")
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("br")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setColumnType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("user_id")
                            .setColumnType(JavaType.STRING)
                            .setJoinSpec(
                                new JoinSpecBuilder()
                                    .setJoinColumn("user_id")
                                    .setJoinTable("users")
                                    .setJoinType(JoinType.INNER_JOIN)
                                    .createJoinSpec()
                            )
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("house_id")
                            .setColumnType(JavaType.STRING)
                            .setJoinSpec(
                                new JoinSpecBuilder()
                                    .setJoinTable("house")
                                    .setJoinType(JoinType.INNER_JOIN)
                                    .setJoinColumn("id")
                                    .createJoinSpec()
                            )
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("supervisor_id")
                            .setJoinSpec(
                                new JoinSpecBuilder()
                                    .setJoinTable("supervisor")
                                    .setJoinColumn("id")
                                    .setJoinType(JoinType.INNER_JOIN)
                                    .createJoinSpec()
                            )
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("supervisor")
                    .setColumnSpecs(
                        ImmutableList.of(
                            new ColumnSpecBuilder()
                                .setColumnName("id")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("supervisor_name")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("ac_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("ac")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec()
                        )
                    )
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("ac")
                    .setColumnSpecs(
                        ImmutableList.of(
                            new ColumnSpecBuilder()
                                .setColumnName("id")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("ac_name")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("area_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("area")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec()
                        )
                    )
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("region")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setColumnType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("region_name")
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("area")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setColumnType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("area_name")
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("region_id")
                            .setColumnType(JavaType.STRING)
                            .setJoinSpec(
                                new JoinSpecBuilder()
                                    .setJoinTable("region")
                                    .setJoinType(JoinType.INNER_JOIN)
                                    .setJoinColumn("id")
                                    .createJoinSpec()
                            )
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("house")
                    .setColumnSpecs(ImmutableList.of(
                        new ColumnSpecBuilder()
                            .setColumnName("id")
                            .setColumnType(JavaType.LONG)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("house_name")
                            .setColumnType(JavaType.STRING)
                            .createColumnSpec(),
                        new ColumnSpecBuilder()
                            .setColumnName("area_id")
                            .setColumnType(JavaType.STRING)
                            .setJoinSpec(
                                new JoinSpecBuilder()
                                    .setJoinTable("area")
                                    .setJoinColumn("id")
                                    .setJoinType(JoinType.INNER_JOIN)
                                    .createJoinSpec()
                            )
                            .createColumnSpec()
                    ))
                    .createTableSpec()
            )
            .add(
                new TableSpecBuilder()
                    .setTableName("contact")
                    .setColumnSpecs(
                        ImmutableList.of(
                            new ColumnSpecBuilder()
                                .setColumnName("id")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("name")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("phone")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("email")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("ptr")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("swp")
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("br_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("br")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("area_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("area")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("house_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("house")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("region_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("region")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("supervisor_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("supervisor")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec(),
                            new ColumnSpecBuilder()
                                .setColumnName("ac_id")
                                .setJoinSpec(
                                    new JoinSpecBuilder()
                                        .setJoinTable("ac")
                                        .setJoinColumn("id")
                                        .createJoinSpec()
                                )
                                .createColumnSpec()
                        )
                    )
                    .createTableSpec()
            )
        ;

        return builder.build();
    }
}
