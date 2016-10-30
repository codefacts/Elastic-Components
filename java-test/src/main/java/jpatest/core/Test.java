package jpatest.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.composer.transformation.impl.json.object.RemoveNullsTransformation;
import elasta.module.ModuleSystem;
import elasta.orm.Db;
import elasta.orm.OrmExporter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import jpatest.models.Ac;
import jpatest.models.Br;
import jpatest.models.Supervisor;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Created by Jango on 10/12/2016.
 */
public class Test {
    private static final io.vertx.core.json.JsonObject DB_CONFIG = new JsonObject("{\n" +
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

            Db db = moduleSystem.require(Db.class);

//            db.count("Br").then(aLong -> System.out.println("count: " + aLong));
//            db.findOne("Br", 1L).then(entries -> System.out.println(entries));
//            db.findAll("Br", Arrays.asList(1L, 2L, 3L)).then(entries -> System.out.println(entries));

//            db.findAll("Br", Arrays.asList(16L, 66L, 116L), ImmutableList.of(
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

            db.insertOrUpdate("Br",
                new RemoveNullsTransformation(null, null)
                    .transform(
                        new JsonObject(
                            mapper.writeValueAsString(
                                Utils.call(() -> {
                                    Br br = new Br();

                                    br.setId(850L);
                                    br.setDateOfBirth(new Date());
                                    br.setEmail("sona@kaku.com");
                                    br.setFirstName("hala");
                                    br.setLastName("kk");

//                                    br.setTablets(
//                                        ImmutableList.of(
//
//                                        )
//                                    );

                                    br.setSupervisor(
                                        Utils.call(() -> {
                                            Supervisor supervisor = new Supervisor();

                                            supervisor.setId(890L);
                                            supervisor.setEmail("spp@dk.com");
                                            supervisor.setFirstName("moni");
                                            supervisor.setLastName("akter");
                                            supervisor.setDateOfBirth(new Date());
                                            supervisor.setJoinDate(new Date());
                                            supervisor.setSalary(8935349.8948);

                                            supervisor.setAc(
                                                Utils.call(() -> {
                                                    Ac ac = new Ac();

                                                    ac.setId(783L);
                                                    ac.setDateOfBirth(new Date());
                                                    ac.setFirstName("komol");
                                                    ac.setLastName("banu");
                                                    ac.setEmail("ac@dd.com");

                                                    return ac;
                                                })
                                            );

                                            return supervisor;
                                        })
                                    );

                                    return br;
                                })
                            )
                        ).put("sohan", "kala")
                    )
            ).then(jsonObject -> System.out.println("result: " + jsonObject)).err(Throwable::printStackTrace);

            System.out.println("DB created.");

            Thread.sleep(Long.MAX_VALUE);

        } finally {

            em.close();

            emf.close();
        }
    }
}
