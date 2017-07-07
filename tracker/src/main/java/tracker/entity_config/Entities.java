package tracker.entity_config;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.entity.core.columnmapping.impl.ColumnMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingOptionsImpl;
import tracker.model.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by sohan on 6/25/2017.
 */
public interface Entities {
    String USER = "User";
    String DEVICE = "Device";
    String POSITION = "Position";
    String USER_TABLE = "users";
    String DEVICE_TABLE = "devices";
    String POSITION_TABLE = "positions";

    static Collection<Entity> entities() {

        return ImmutableList.of(userEntity(), deviceEntity(), positionEntity());
    }

    static Entity positionEntity() {
        return new Entity(
            POSITION,
            PositionModel.id,
            ArrayBuilder.<Field>create()
                .addAll(Arrays.asList(
                    new Field(PositionModel.lat, JavaType.DOUBLE),
                    new Field(PositionModel.lng, JavaType.DOUBLE),
                    new Field(PositionModel.accuracy, JavaType.DOUBLE),
                    new Field(PositionModel.time, JavaType.DOUBLE),
                    new Field(PositionModel.altitude, JavaType.DOUBLE),
                    new Field(PositionModel.speed, JavaType.STRING),
                    new Field(PositionModel.provider, JavaType.STRING),
                    new Field(PositionModel.batteryLevel, JavaType.DOUBLE)
                ))
                .addAll(baseFields())
                .build(toFieldsArray()),
            new DbMapping(
                POSITION_TABLE,
                PositionTable.id,
                ArrayBuilder.<ColumnMapping>create()
                    .addAll(Arrays.asList(
                        new ColumnMappingImpl(PositionModel.lat, PositionTable.lat, DbType.NUMBER),
                        new ColumnMappingImpl(PositionModel.lng, PositionTable.lng, DbType.DATETIME),
                        new ColumnMappingImpl(PositionModel.accuracy, PositionTable.accuracy, DbType.DATETIME),
                        new ColumnMappingImpl(PositionModel.time, PositionTable.time, DbType.DATETIME),
                        new ColumnMappingImpl(PositionModel.altitude, PositionTable.altitude, DbType.DATETIME),
                        new ColumnMappingImpl(PositionModel.speed, PositionTable.speed, DbType.DATETIME),
                        new ColumnMappingImpl(PositionModel.provider, PositionTable.provider, DbType.DATETIME),
                        new ColumnMappingImpl(PositionModel.batteryLevel, PositionTable.battery_level, DbType.DATETIME)
                    ))
                    .addAll(baseColumns())
                    .build(toColumnsArray()),
                baseRelationMappingsArray()
            )
        );
    }

    static Entity userEntity() {
        return new Entity(
            USER,
            UserModel.id,
            new ArrayBuilderImpl<Field>()
                .addAll(Arrays.asList(
                    new Field(UserModel.userId, JavaType.STRING),
                    new Field(UserModel.username, JavaType.STRING),
                    new Field(UserModel.password, JavaType.STRING),

                    new Field(UserModel.firstName, JavaType.STRING),
                    new Field(UserModel.lastName, JavaType.STRING),
                    new Field(UserModel.email, JavaType.STRING),
                    new Field(UserModel.phone, JavaType.STRING),

                    new Field(UserModel.dateOfBirth, JavaType.STRING),
                    new Field(UserModel.gender, JavaType.STRING),
                    new Field(UserModel.registrationSource, JavaType.STRING)
                ))
                .addAll(baseFields())
                .build(list -> list.toArray(new Field[list.size()])),
            new DbMapping(
                USER_TABLE,
                UserTable.id,
                new ArrayBuilderImpl<ColumnMapping>()
                    .addAll(Arrays.asList(
                        new ColumnMappingImpl(UserModel.userId, UserTable.user_id, DbType.VARCHAR),
                        new ColumnMappingImpl(UserModel.username, UserTable.username, DbType.VARCHAR),
                        new ColumnMappingImpl(UserModel.password, UserTable.password, DbType.VARCHAR),

                        new ColumnMappingImpl(UserModel.firstName, UserTable.first_name, DbType.VARCHAR),
                        new ColumnMappingImpl(UserModel.lastName, UserTable.last_name, DbType.VARCHAR),
                        new ColumnMappingImpl(UserModel.email, UserTable.email, DbType.VARCHAR),
                        new ColumnMappingImpl(UserModel.phone, UserTable.phone, DbType.VARCHAR),

                        new ColumnMappingImpl(UserModel.dateOfBirth, UserTable.date_of_birth, DbType.VARCHAR),
                        new ColumnMappingImpl(UserModel.gender, UserTable.gender, DbType.VARCHAR),
                        new ColumnMappingImpl(UserModel.registrationSource, UserTable.registration_source, DbType.VARCHAR)
                    ))
                    .addAll(baseColumns())
                    .build(list -> list.toArray(new ColumnMapping[list.size()])),
                baseRelationMappingsArray()
            )
        );
    }

    static Entity deviceEntity() {
        return new Entity(
            DEVICE,
            DeviceModel.id,
            new ArrayBuilderImpl<Field>()
                .add(new Field(DeviceModel.deviceId, JavaType.STRING))
                .addAll(baseFields())
                .build(toFieldsArray()),
            new DbMapping(
                DEVICE_TABLE,
                DeviceTable.id,
                ArrayBuilder.<ColumnMapping>create()
                    .add(new ColumnMappingImpl(DeviceModel.deviceId, DeviceTable.device_id, DbType.VARCHAR))
                    .addAll(baseColumns())
                    .build(toColumnsArray()),
                baseRelationMappingsArray()
            )
        );
    }

    static ArrayBuilder.CreateArrayFunc<ColumnMapping> toColumnsArray() {
        return list -> list.toArray(new ColumnMapping[list.size()]);
    }

    static ArrayBuilder.CreateArrayFunc<Field> toFieldsArray() {
        return list -> list.toArray(new Field[list.size()]);
    }

    static RelationMapping[] baseRelationMappingsArray() {
        return new RelationMapping[]{
            createdBy(),
            updatedBy(),
        };
    }

    static Field updatedByField() {
        return new Field(UserModel.updatedBy, JavaType.OBJECT, Optional.of(new Relationship(
            Relationship.Type.MANY_TO_ONE, Relationship.Name.HAS_ONE, USER
        )));
    }

    static Field createdByField() {
        return new Field(UserModel.createdBy, JavaType.OBJECT, Optional.of(new Relationship(
            Relationship.Type.MANY_TO_ONE, Relationship.Name.HAS_ONE, USER
        )));
    }

    static RelationMapping updatedBy() {
        return new DirectRelationMappingImpl(
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
                DirectRelationMappingOptions.LoadAndDelete.NO_OPERATION
            )
        );
    }

    static RelationMapping createdBy() {
        return new DirectRelationMappingImpl(
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
                DirectRelationMappingOptions.LoadAndDelete.NO_OPERATION
            )
        );
    }

    static List<Field> baseFields() {
        return Arrays.asList(
            new Field(UserModel.id, JavaType.LONG),
            new Field(UserModel.createDate, JavaType.STRING),
            new Field(UserModel.updateDate, JavaType.STRING),
            createdByField(),
            updatedByField()
        );
    }

    static List<ColumnMapping> baseColumns() {
        return Arrays.asList(
            new ColumnMappingImpl(UserModel.id, UserTable.id, DbType.NUMBER),
            new ColumnMappingImpl(UserModel.createDate, UserTable.create_date, DbType.DATETIME),
            new ColumnMappingImpl(UserModel.updateDate, UserTable.update_date, DbType.DATETIME)
        );
    }
}
