package tracker.entity_config;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMappingOptions;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingImpl;
import elasta.orm.entity.core.columnmapping.impl.DirectRelationMappingOptionsImpl;
import elasta.orm.entity.core.columnmapping.impl.RelationMappingOptionsImpl;
import elasta.orm.entity.core.columnmapping.impl.VirtualRelationMappingImpl;
import tracker.model.merchandiser.*;

import java.util.Arrays;

import static tracker.entity_config.Entities.*;

/**
 * Created by sohan on 7/18/2017.
 */
public interface MerchandiserEntities {

    static Entity outletEntity() {
        return new Entity(
            Entities.OUTLET_ENTITY,
            OutletModel.id,
            ArrayBuilder.<Field>create()
                .addAll(ImmutableList.of(
                    field(OutletModel.name),
                    field(OutletModel.address),
                    field(OutletModel.qrCode),
                    field(OutletModel.location, hasOneLocation()),
                    field(OutletModel.locationGps, hasOneLocation()),
                    field(OutletModel.locationNetwork, hasOneLocation()),
                    field(OutletModel.images, hasManyOutletImages())
                ))
                .addAll(Entities.baseFields())
                .build(list -> list.toArray(new Field[list.size()])),
            new DbMapping(
                Entities.OUTLET_TABLE,
                OutletTable.id,
                ArrayBuilder.<ColumnMapping>create()
                    .addAll(ImmutableList.of(
                        column(OutletModel.name, OutletTable.name),
                        column(OutletModel.address, OutletTable.address),
                        column(OutletModel.qrCode, OutletTable.qr_code)
                    ))
                    .addAll(Entities.baseColumns())
                    .build(list -> list.toArray(new ColumnMapping[list.size()])),
                ArrayBuilder.<RelationMapping>create()
                    .addAll(ImmutableList.of(
                        Entities.locationMapping(OutletModel.location, OutletTable.location_id),
                        Entities.locationMapping(OutletModel.locationGps, OutletTable.location_id_gps),
                        Entities.locationMapping(OutletModel.locationNetwork, OutletTable.location_id_network),
                        outletImagesMapping()
                    ))
                    .addAll(Arrays.asList(Entities.baseRelationMappingsArray()))
                    .build(list -> list.toArray(new RelationMapping[list.size()]))
            )
        );
    }

    static Relationship hasOneLocation() {
        return new Relationship(
            Relationship.Type.MANY_TO_ONE,
            Relationship.Name.HAS_ONE,
            Entities.LOCATION_ENTITY
        );
    }

    static Relationship hasManyOutletImages() {
        return new Relationship(
            Relationship.Type.ONE_TO_MANY,
            Relationship.Name.HAS_MANY,
            Entities.OUTLET_IMAGE_ENTITY
        );
    }

    static VirtualRelationMappingImpl outletImagesMapping() {
        return new VirtualRelationMappingImpl(
            Entities.OUTLET_IMAGE_TABLE,
            Entities.OUTLET_IMAGE_ENTITY,
            ImmutableList.of(
                new ForeignColumnMapping(
                    OutletImageTable.outlet_id,
                    OutletTable.id
                )
            ),
            OutletModel.images,
            new RelationMappingOptionsImpl(
                RelationMappingOptions.CascadeUpsert.YES,
                RelationMappingOptions.CascadeDelete.YES,
                true
            )
        );
    }

    static Entity outletImageEntity() {
        return new Entity(
            Entities.OUTLET_IMAGE_ENTITY,
            OutletImageModel.id,
            ArrayBuilder.<Field>create()
                .addAll(ImmutableList.of(
                    field(OutletImageModel.title),
                    field(OutletImageModel.description),
                    field(OutletImageModel.uri),
                    field(OutletImageModel.file),
                    field(OutletImageModel.fileName),
                    field(OutletImageModel.height),
                    field(OutletImageModel.width),
                    field(OutletImageModel.outlet, hasOneOutlet())
                ))
                .addAll(Entities.baseFields())
                .build(list -> new Field[list.size()]),
            new DbMapping(
                Entities.OUTLET_IMAGE_TABLE,
                OutletImageTable.id,
                ArrayBuilder.<ColumnMapping>create()
                    .addAll(ImmutableList.of(
                        column(OutletImageModel.title, OutletImageTable.title),
                        column(OutletImageModel.description, OutletImageTable.description),
                        column(OutletImageModel.uri, OutletImageTable.uri),
                        column(OutletImageModel.file, OutletImageTable.file),
                        column(OutletImageModel.fileName, OutletImageTable.file_name),
                        column(OutletImageModel.height, OutletImageTable.height),
                        column(OutletImageModel.width, OutletImageTable.width)
                    ))
                    .addAll(Entities.baseColumns())
                    .build(list -> list.toArray(new ColumnMapping[list.size()])),
                ArrayBuilder.<RelationMapping>create()
                    .addAll(ImmutableList.of(
                        new DirectRelationMappingImpl(
                            OUTLET_TABLE,
                            OUTLET_ENTITY,
                            ImmutableList.of(
                                new ForeignColumnMapping(
                                    OutletImageTable.outlet_id,
                                    OutletTable.id
                                )
                            ),
                            OutletImageModel.outlet,
                            new DirectRelationMappingOptionsImpl(
                                RelationMappingOptions.CascadeUpsert.NO,
                                RelationMappingOptions.CascadeDelete.NO,
                                true,
                                DirectRelationMappingOptions.LoadAndDelete.LOAD_AND_DELETE
                            )
                        )
                    ))
                    .addAll(Arrays.asList(Entities.baseRelationMappingsArray()))
                    .build(list -> list.toArray(new RelationMapping[list.size()]))
            )
        );
    }

    static Relationship hasOneOutlet() {
        return new Relationship(
            Relationship.Type.MANY_TO_ONE,
            Relationship.Name.HAS_ONE,
            OUTLET_ENTITY
        );
    }

    static Entity locationEntity() {
        return new Entity(
            LOCATION_ENTITY,
            LocationModel.id,
            ArrayBuilder.<Field>create()
                .addAll(ImmutableList.of(
                    Entities.field(LocationModel.lat),
                    Entities.field(LocationModel.lng),
                    Entities.field(LocationModel.accuracy)
                ))
                .addAll(Entities.baseFields())
                .build(list -> list.toArray(new Field[list.size()])),
            new DbMapping(
                LOCATION_TABLE,
                LocationTable.id,
                ArrayBuilder.<ColumnMapping>create()
                    .addAll(ImmutableList.of(
                        Entities.column(LocationModel.lat, LocationTable.lat),
                        Entities.column(LocationModel.lng, LocationTable.lng),
                        Entities.column(LocationModel.accuracy, LocationTable.accuracy)
                    ))
                    .addAll(Entities.baseColumns())
                    .build(list -> list.toArray(new ColumnMapping[list.size()])),
                Entities.baseRelationMappingsArray()
            )
        );
    }
}
