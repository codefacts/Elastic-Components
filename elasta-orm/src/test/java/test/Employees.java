package test;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.JavaType;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.impl.DirectDbColumnMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.IndirectDbColumnMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.SimpleDbColumnMappingImpl;
import elasta.sql.core.*;

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
                    .dbColumnMappings(new DbColumnMapping[]{
                        new SimpleDbColumnMappingImpl(
                            "eid", "EID".toUpperCase(), DbType.NUMBER
                        ),
                        new SimpleDbColumnMappingImpl(
                            "ename", "ename".toUpperCase(), DbType.VARCHAR
                        ),
                        new SimpleDbColumnMappingImpl(
                            "salary", "salary".toUpperCase(), DbType.NUMBER
                        ),
                        new SimpleDbColumnMappingImpl(
                            "deg", "deg".toUpperCase(), DbType.VARCHAR
                        ),
                        new DirectDbColumnMappingImpl(
                            "DEPARTMENT",
                            "department",
                            ImmutableList.of(
                                new ForeignColumnMapping(
                                    "E_DEPARTMENT_ID", "ID"
                                )
                            ),
                            "department"
                        ),
                        new DirectDbColumnMappingImpl(
                            "DEPARTMENT",
                            "department",
                            ImmutableList.of(
                                new ForeignColumnMapping(
                                    "E_DEPARTMENT2_ID", "ID"
                                )
                            ),
                            "department2"
                        ),
                        new IndirectDbColumnMappingImpl(
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
                            "departments"
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
                new DbColumnMapping[]{
                    new SimpleDbColumnMappingImpl(
                        "id", "ID", DbType.NUMBER
                    ),
                    new SimpleDbColumnMappingImpl(
                        "name", "NAME", DbType.VARCHAR
                    ),
                    new DirectDbColumnMappingImpl(
                        "DEPARTMENT",
                        "department",
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                "D_DEPARTMENT_ID", "ID"
                            )
                        ),
                        "department"
                    ),
                    new DirectDbColumnMappingImpl(
                        "EMPLOYEE",
                        "employee",
                        ImmutableList.of(
                            new ForeignColumnMapping(
                                "D_EMPLOYEE_EID", "EID"
                            )
                        ),
                        "employee"
                    )
                }
            )
        );

        return Arrays.asList(employee, department);
    }
}
