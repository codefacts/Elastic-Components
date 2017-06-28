package test;

import elasta.core.promise.impl.Promises;
import elasta.orm.event.EventProcessor;
import elasta.orm.event.builder.impl.EventProcessorBuilderImpl;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 4/3/2017.
 */
public interface EventProcessorTest {
    static void main(String[] asdf) {

        EventProcessorBuilderImpl builder = new EventProcessorBuilderImpl(
            Test.helper()
        );

        EventProcessor processor = builder
            .onDelete("employee", params -> {

                System.out.println("employee: " + params.getPath() + " -> " + params.getEntity());

                return Promises.of(
                    params.getEntity()
                );
            })
            .onDelete("department", params -> {

                System.out.println("department: " + params.getPath() + " -> " + params.getEntity());

                return Promises.of(
                    new JsonObject()
                );
            })
            .onDeleteRelation("employee", params -> {

                System.out.println("deleteRelation: employee: " + params.getPath() + " -> " + params.getEntity());

                return Promises.of(
                    params.getEntity()
                );
            })
            .build();

        processor.processDelete(
            "employee",
            new JsonObject(
                "{\"eid\":1201,\"ename\":\"Gopal\",\"salary\":40000.0,\"deg\":\"Technical Manager\",\"department\":{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"department2\":{\"id\":988286326887,\"name\":\"BGGV\",\"department\":{\"id\":8283175518,\"name\":\"MKLC\",\"department\":{\"id\":56165582,\"name\":\"VVKM\",\"department\":null,\"employee\":{\"eid\":2389,\"ename\":\"KOMOL\",\"salary\":8000.0,\"deg\":\"DOC\",\"department\":null,\"department2\":null,\"departments\":[]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"departments\":[{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}}]}"
            )
        );

        processor.processDeleteRelation("employee", new JsonObject(
                "{\"eid\":1201,\"ename\":\"Gopal\",\"salary\":40000.0,\"deg\":\"Technical Manager\",\"department\":{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"department2\":{\"id\":988286326887,\"name\":\"BGGV\",\"department\":{\"id\":8283175518,\"name\":\"MKLC\",\"department\":{\"id\":56165582,\"name\":\"VVKM\",\"department\":null,\"employee\":{\"eid\":2389,\"ename\":\"KOMOL\",\"salary\":8000.0,\"deg\":\"DOC\",\"department\":null,\"department2\":null,\"departments\":[]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"departments\":[{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}}]}"
            )
        );

    }
}
