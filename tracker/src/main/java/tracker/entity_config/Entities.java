package tracker.entity_config;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.entity.core.columnmapping.impl.ColumnMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingOptionsImpl;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by sohan on 6/25/2017.
 */
public interface Entities {
    static Collection<Entity> entities() {
        Entity user = new Entity(
            "user",
            "id",
            new Field[]{
                new Field("id", JavaType.LONG),
                new Field("userId", JavaType.STRING),
                new Field("username", JavaType.STRING),
                new Field("email", JavaType.STRING),
                new Field("phone", JavaType.STRING),
                new Field("createDate", JavaType.STRING),
                new Field("updateDate", JavaType.STRING),
                new Field("createdBy", JavaType.OBJECT, Optional.of(new Relationship(
                    Relationship.Type.MANY_TO_ONE, Relationship.Name.HAS_ONE, "user"
                ))),
                new Field("updatedBy", JavaType.OBJECT, Optional.of(new Relationship(
                    Relationship.Type.MANY_TO_ONE, Relationship.Name.HAS_ONE, "user"
                )))
            },
            new DbMapping(
                "users",
                "id",
                new ColumnMapping[]{
                    new ColumnMappingImpl("id", "id", DbType.NUMBER),
                    new ColumnMappingImpl("userId", "user_id", DbType.VARCHAR),
                    new ColumnMappingImpl("username", "username", DbType.VARCHAR),
                    new ColumnMappingImpl("email", "email", DbType.VARCHAR),
                    new ColumnMappingImpl("phone", "phone", DbType.VARCHAR),
                    new ColumnMappingImpl("createDate", "create_date", DbType.DATETIME),
                    new ColumnMappingImpl("updateDate", "update_date", DbType.DATETIME),
                },
                new RelationMapping[]{
                    new DirectRelationMappingImpl(
                        "users",
                        "user",
                        ImmutableList.of(
                            new ForeignColumnMapping("created_by", "id")
                        ),
                        "createdBy",
                        new DirectRelationMappingOptionsImpl(
                            RelationMappingOptions.CascadeUpsert.NO,
                            RelationMappingOptions.CascadeDelete.NO,
                            true,
                            DirectRelationMappingOptions.LoadAndDeleteParent.NO_OPERATION
                        )
                    ),
                    new DirectRelationMappingImpl(
                        "users",
                        "user",
                        ImmutableList.of(
                            new ForeignColumnMapping("updated_by", "id")
                        ),
                        "updatedBy",
                        new DirectRelationMappingOptionsImpl(
                            RelationMappingOptions.CascadeUpsert.NO,
                            RelationMappingOptions.CascadeDelete.NO,
                            true,
                            DirectRelationMappingOptions.LoadAndDeleteParent.NO_OPERATION
                        )
                    ),
                }
            )
        );

        return ImmutableList.of(user);
    }
}
