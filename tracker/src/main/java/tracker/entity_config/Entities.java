package tracker.entity_config;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.entity.core.columnmapping.impl.ColumnMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingOptionsImpl;
import tracker.model.UserModel;
import tracker.model.UserTable;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by sohan on 6/25/2017.
 */
public interface Entities {
    String USER = "User";
    String USER_TABLE = "users";

    static Collection<Entity> entities() {
        Entity User = new Entity(
            USER,
            UserModel.id,
            new Field[]{
                new Field(UserModel.id, JavaType.LONG),
                new Field(UserModel.userId, JavaType.STRING),
                new Field(UserModel.username, JavaType.STRING),
                new Field(UserModel.email, JavaType.STRING),
                new Field(UserModel.phone, JavaType.STRING),
                new Field(UserModel.createDate, JavaType.STRING),
                new Field(UserModel.updateDate, JavaType.STRING),
                new Field(UserModel.createdBy, JavaType.OBJECT, Optional.of(new Relationship(
                    Relationship.Type.MANY_TO_ONE, Relationship.Name.HAS_ONE, USER
                ))),
                new Field(UserModel.updatedBy, JavaType.OBJECT, Optional.of(new Relationship(
                    Relationship.Type.MANY_TO_ONE, Relationship.Name.HAS_ONE, USER
                )))
            },
            new DbMapping(
                USER_TABLE,
                UserTable.id,
                new ColumnMapping[]{
                    new ColumnMappingImpl(UserModel.id, UserTable.id, DbType.NUMBER),
                    new ColumnMappingImpl(UserModel.userId, UserTable.user_id, DbType.VARCHAR),
                    new ColumnMappingImpl(UserModel.username, UserTable.username, DbType.VARCHAR),
                    new ColumnMappingImpl(UserModel.email, UserTable.email, DbType.VARCHAR),
                    new ColumnMappingImpl(UserModel.phone, UserTable.phone, DbType.VARCHAR),
                    new ColumnMappingImpl(UserModel.createDate, UserTable.create_date, DbType.DATETIME),
                    new ColumnMappingImpl(UserModel.updateDate, UserTable.update_date, DbType.DATETIME),
                },
                new RelationMapping[]{
                    new DirectRelationMappingImpl(
                        USER_TABLE,
                        USER,
                        ImmutableList.of(
                            new ForeignColumnMapping(UserTable.created_by, UserTable.id)
                        ),
                        UserModel.createdBy,
                        new DirectRelationMappingOptionsImpl(
                            RelationMappingOptions.CascadeUpsert.NO,
                            RelationMappingOptions.CascadeDelete.NO,
                            true,
                            DirectRelationMappingOptions.LoadAndDeleteParent.NO_OPERATION
                        )
                    ),
                    new DirectRelationMappingImpl(
                        USER_TABLE,
                        USER,
                        ImmutableList.of(
                            new ForeignColumnMapping(UserTable.updated_by, UserTable.id)
                        ),
                        UserModel.updatedBy,
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

        return ImmutableList.of(User);
    }
}
