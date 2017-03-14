package jpatest.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.orm.Orm;
import elasta.pipeline.transformation.impl.json.object.RemoveNullsTransformation;
import elasta.module.ModuleSystem;
import elasta.orm.OrmExporter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import jpatest.models.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jango on 10/12/2016.
 */
public class Test {
    public static final JsonObject DB_CONFIG = new JsonObject("{\n" +
        "  \"url\": \"jdbc:mysql://localhost:3306/jpadb\",\n" +
        "  \"user\": \"root\",\n" +
        "  \"password\": \"\",\n" +
        "  \"driver_class\": \"com.mysql.jdbc.Driver\"\n" +
        "}");

    public static void main(String[] args) throws InterruptedException, JsonProcessingException {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory(Main.PU);

        EntityManager em = emf.createEntityManager();

        try {

            ModuleSystem moduleSystem = ModuleSystem.create();

            OrmExporter.exportModule(moduleSystem);

            moduleSystem.export(EntityManagerFactory.class, module -> module.export(emf));
            moduleSystem.export(Vertx.class, module -> module.export(Vertx.vertx()));
            moduleSystem.export(JDBCClient.class, module -> module.export(JDBCClient.createShared(module.require(Vertx.class), DB_CONFIG)));
            moduleSystem.export(ObjectMapper.class, module -> module.export(new ObjectMapper()));

            Orm orm = moduleSystem.require(Orm.class);

//            orm.count("Br").then(aLong -> System.out.println("count: " + aLong));
//            orm.findOne("Br", 1L).then(entries -> System.out.println(entries));
//            orm.findAll("Br", Arrays.asList(1L, 2L, 3L)).then(entries -> System.out.println(entries));

//            orm.findAll("Br", Arrays.asList(16L, 66L, 116L), ImmutableList.of(
//                new FieldInfoBuilder()
//                    .setFields(Arrays.asList("firstName", "lastName", "email", "phone", "dateOfBirth"))
//                    .setPath("")
//                    .createSqlField(),
//                new FieldInfoBuilder()
//                    .setFields(Arrays.asList("buyDate", "id", "price", "name"))
//                    .setPath("tablets")
//                    .createSqlField()
//            )).then(entries -> System.out.println(entries)).err(e -> e.printStackTrace());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(dateFormat);

            orm.insertOrUpdate("Br",
                new RemoveNullsTransformation(null, null)
                    .transform(
                        new JsonObject(
                            mapper.writeValueAsString(
                                Utils.call(() -> {
                                    Br br = new Br();

                                    br.setId(225588L);
                                    br.setDateOfBirth(new Date());
                                    br.setEmail("sona@kaku.com");
                                    br.setFirstName("br@851");
                                    br.setLastName("kk");

                                    br.setTablets(
                                        ImmutableList.of(

                                            Utils.call(() -> {
                                                Tablet tablet = new Tablet();

                                                tablet.setId(898L);
                                                tablet.setName("T#898");

                                                return tablet;
                                            }),
                                            Utils.call(() -> {
                                                Tablet tablet = new Tablet();

                                                tablet.setId(599L);
                                                tablet.setName("T@896");

                                                return tablet;
                                            }),
                                            Utils.call(() -> {
                                                Tablet tablet = new Tablet();

                                                tablet.setId(900L);
                                                tablet.setName("T@896");

                                                return tablet;
                                            })
                                        )
                                    );

                                    br.setSupervisor(
                                        Utils.call(() -> {
                                            Supervisor supervisor = new Supervisor();

                                            supervisor.setId(895L);
                                            supervisor.setEmail("spp@dk.com");
                                            supervisor.setFirstName("sup895");
                                            supervisor.setLastName("akter");
                                            supervisor.setDateOfBirth(new Date());
                                            supervisor.setJoinDate(new Date());
                                            supervisor.setSalary(8935349.8948);

                                            supervisor.setAc(
                                                Utils.call(() -> {
                                                    Ac ac = new Ac();

                                                    ac.setId(775L);
                                                    ac.setDateOfBirth(new Date());
                                                    ac.setFirstName("ac@775");
                                                    ac.setLastName("banu");
                                                    ac.setEmail("ac@dd.com");

                                                    ac.setArea(Utils.call(() -> {
                                                        Area area = new Area();

                                                        area.setId(91L);
                                                        area.setName("Area@91");
                                                        area.setRegion(Utils.call(() -> {
                                                            Region region = new Region();

                                                            region.setId(89L);
                                                            region.setName("Region@89");

                                                            return region;
                                                        }));

                                                        return area;
                                                    }));

                                                    return ac;
                                                })
                                            );

                                            return supervisor;
                                        })
                                    );

                                    br.setHouse(Utils.call(() -> {
                                        House house = new House();

                                        house.setId(566L);
                                        house.setName("house@566");

                                        house.setArea(Utils.call(() -> {
                                            Area area = new Area();

                                            area.setId(91L);

                                            return area;
                                        }));

                                        return house;
                                    }));

                                    br.setCommands(
                                        ImmutableList.of(
                                            Utils.call(() -> {
                                                Command command = new Command();

                                                command.setId(568L);
                                                command.setName("comd@568");

                                                return command;
                                            }),
                                            Utils.call(() -> {
                                                Command command = new Command();

                                                command.setId(569L);
                                                command.setName("comd$569");

                                                return command;
                                            }),
                                            Utils.call(() -> {
                                                Command command = new Command();

                                                command.setId(570L);
                                                command.setName("comd+570");

                                                return command;
                                            }),
                                            Utils.call(() -> {
                                                Command command = new Command();

                                                command.setId(520L);
                                                command.setName("comd+520");

                                                return command;
                                            })
                                        )
                                    );

                                    return br;
                                })
                            )
                        ).put("sohan", "kala")
                    )
            ).then(jsonObject -> System.out.println("result: " + jsonObject))
                .err((throwable) -> throwable.printStackTrace());

            System.out.println("DB created.");

            Thread.sleep(Long.MAX_VALUE);

        } finally {

            em.close();

            emf.close();
        }
    }
}
