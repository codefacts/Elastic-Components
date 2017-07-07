package test;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.JavaType;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import elasta.orm.entity.core.columnmapping.RelationMappingOptions;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingImpl;
import elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingOptionsImpl;
import elasta.orm.entity.core.columnmapping.impl.IndirectRelationMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.ColumnMappingImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by sohan on 3/22/2017.
 */
public interface Employees {
    static List<Entity> entities() {

        Entity employee = Entity.builder()
            .name("employee")
            .primaryKey("eid")
            .fields(new Field[]{
                new Field(
                    "eid", JavaType.INTEGER, Optional.empty()
                ),
                new Field(
                    "ename", JavaType.STRING, Optional.empty()
                ),
                new Field(
                    "salary", JavaType.DOUBLE, Optional.empty()
                ),
                new Field(
                    "deg", JavaType.STRING, Optional.empty()
                ),
                new Field(
                    "department", JavaType.OBJECT,
                    Optional.of(
                        new Relationship(
                            Relationship.Type.MANY_TO_ONE,
                            Relationship.Name.HAS_ONE,
                            "department"
                        )
                    )
                ),
                new Field(
                    "department2", JavaType.OBJECT,
                    Optional.of(
                        new Relationship(
                            Relationship.Type.MANY_TO_ONE,
                            Relationship.Name.HAS_ONE,
                            "department"
                        )
                    )
                ),
                new Field(
                    "departments", JavaType.ARRAY,
                    Optional.of(
                        new Relationship(
                            Relationship.Type.MANY_TO_MANY,
                            Relationship.Name.HAS_MANY,
                            "department"
                        )
                    )
                )
            })
            .dbMapping(
                DbMapping.builder()
                    .table("EMPLOYEE")
                    .primaryColumn("EID")
                    .columnMappings(new ColumnMapping[]{
                        new ColumnMappingImpl(
                            "eid", "EID".toUpperCase(), DbType.NUMBER
                        ),
                        new ColumnMappingImpl(
                            "ename", "ename".toUpperCase(), DbType.VARCHAR
                        ),
                        new ColumnMappingImpl(
                            "salary", "salary".toUpperCase(), DbType.NUMBER
                        ),
                        new ColumnMappingImpl(
                            "deg", "deg".toUpperCase(), DbType.VARCHAR
                        )
                    })
                    .relationMappings(new RelationMapping[]{
                        new DirectRelationMappingImpl(
                            "DEPARTMENT",
                            "department",
                            ImmutableList.of(
                                new ForeignColumnMapping(
                                    "E_DEPARTMENT_ID", "ID"
                                )
                            ),
                            "department",
                            new DirectRelationMappingOptionsImpl(
                                RelationMappingOptions.CascadeUpsert.YES,
                                RelationMappingOptions.CascadeDelete.YES,
                                false, DirectRelationMappingOptions.LoadAndDelete.LOAD_AND_DELETE
                            )
                        ),
                        new DirectRelationMappingImpl(
                            "DEPARTMENT",
                            "department",
                            ImmutableList.of(
                                new ForeignColumnMapping(
                                    "E_DEPARTMENT2_ID", "ID"
                                )
                            ),
                            "department2",
                            new DirectRelationMappingOptionsImpl(
                                RelationMappingOptions.CascadeUpsert.YES,
                                RelationMappingOptions.CascadeDelete.YES,
                                false, DirectRelationMappingOptions.LoadAndDelete.LOAD_AND_DELETE
                            )
                        ),
                        new IndirectRelationMappingImpl(
                            "DEPARTMENT",
                            "department",
                            "EMPLOYEE_DEPARTMENT",
                            ImmutableList.of(
                                new ForeignColumnMapping(
                                    "EID", "EMPLOYEE_EID"
                                )
                            ),
                            ImmutableList.of(
                                new ForeignColumnMapping(
                                    "ID", "DEPARTMENTS_ID"
                                )
                            ),
                            "departments",
                            new DirectRelationMappingOptionsImpl(
                                RelationMappingOptions.CascadeUpsert.YES,
                                RelationMappingOptions.CascadeDelete.YES,
                                false, DirectRelationMappingOptions.LoadAndDelete.LOAD_AND_DELETE
                            )
                        )
                    })
                    .build()
            )
            .build();

        Entity department = new Entity(
            "department",
            "id",
            new Field[]{
                new Field(
                    "id", JavaType.LONG
                ),
                new Field(
                    "name", JavaType.STRING
                ),
                new Field(
                    "department", JavaType.OBJECT,
                    Optional.of(
                        new Relationship(
                            Relationship.Type.MANY_TO_ONE,
                            Relationship.Name.HAS_ONE,
                            "department"
                        )
                    )
                ),
                new Field(
                    "employee", JavaType.OBJECT,
                    Optional.of(
                        new Relationship(
                            Relationship.Type.MANY_TO_ONE,
                            Relationship.Name.HAS_ONE,
                            "employee"
                        )
                    )
                )
            },
            new DbMapping(
                "DEPARTMENT",
                "ID",
                new ColumnMapping[]{
                    new ColumnMappingImpl(
                        "id", "ID", DbType.NUMBER
                    ),
                    new ColumnMappingImpl(
                        "name", "NAME", DbType.VARCHAR
                    )
                },
                new RelationMapping[]{
                    new DirectRelationMappingImpl(
                        "DEPARTMENT",
                        "department",
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                "D_DEPARTMENT_ID", "ID"
                            )
                        ),
                        "department",
                        new DirectRelationMappingOptionsImpl(
                            RelationMappingOptions.CascadeUpsert.YES,
                            RelationMappingOptions.CascadeDelete.YES,
                            false, DirectRelationMappingOptions.LoadAndDelete.LOAD_AND_DELETE
                        )
                    ),
                    new DirectRelationMappingImpl(
                        "EMPLOYEE",
                        "employee",
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                "D_EMPLOYEE_EID", "EID"
                            )
                        ),
                        "employee",
                        new DirectRelationMappingOptionsImpl(
                            RelationMappingOptions.CascadeUpsert.YES,
                            RelationMappingOptions.CascadeDelete.YES,
                            false, DirectRelationMappingOptions.LoadAndDelete.LOAD_AND_DELETE
                        )
                    )
                }
            )
        );

        return Arrays.asList(employee, department);
    }
}
