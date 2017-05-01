package test;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/1/2017.
 */
public interface TestJsEquals {
    static void main(String[] asf) {
        JsonObject jsonObject = new JsonObject("{\n" +
            "  \"eid\" : 1201,\n" +
            "  \"ename\" : \"Gopal\",\n" +
            "  \"deg\" : \"Technical Manager\",\n" +
            "  \"salary\" : 40000.0,\n" +
            "  \"department\" : {\n" +
            "    \"id\" : 98798079087,\n" +
            "    \"name\" : \"ICT\",\n" +
            "    \"employee\" : {\n" +
            "      \"eid\" : 5258,\n" +
            "      \"ename\" : \"Russel\",\n" +
            "      \"salary\" : 52000.0\n" +
            "    },\n" +
            "    \"department\" : {\n" +
            "      \"id\" : 98457984,\n" +
            "      \"name\" : \"RGV\",\n" +
            "      \"employee\" : {\n" +
            "        \"ename\" : \"Russel\",\n" +
            "        \"salary\" : 52000.0\n" +
            "      },\n" +
            "      \"department\" : {\n" +
            "        \"id\" : 94504975049,\n" +
            "        \"name\" : \"MCE\",\n" +
            "        \"employee\" : {\n" +
            "          \"ename\" : \"Russel\",\n" +
            "          \"salary\" : 52000.0\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"department2\" : {\n" +
            "    \"id\" : 988286326887,\n" +
            "    \"name\" : \"BGGV\",\n" +
            "    \"employee\" : {\n" +
            "      \"eid\" : 5258,\n" +
            "      \"ename\" : \"Russel\"\n" +
            "    },\n" +
            "    \"department\" : {\n" +
            "      \"id\" : 8283175518,\n" +
            "      \"name\" : \"MKLC\",\n" +
            "      \"employee\" : {\n" +
            "        \"eid\" : 5258,\n" +
            "        \"ename\" : \"Russel\"\n" +
            "      },\n" +
            "      \"department\" : {\n" +
            "        \"id\" : 56165582,\n" +
            "        \"name\" : \"VVKM\",\n" +
            "        \"employee\" : {\n" +
            "          \"eid\" : 2389,\n" +
            "          \"ename\" : \"KOMOL\",\n" +
            "          \"salary\" : 8000.0\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}");

        JsonObject js2 = new JsonObject("{\n" +
            "  \"eid\" : 1201,\n" +
            "  \"ename\" : \"Gopal\",\n" +
            "  \"deg\" : \"Technical Manager\",\n" +
            "  \"salary\" : 40000.0,\n" +
            "  \"department2\" : {\n" +
            "    \"name\" : \"BGGV\",\n" +
            "    \"id\" : 988286326887,\n" +
            "    \"department\" : {\n" +
            "      \"name\" : \"MKLC\",\n" +
            "      \"id\" : 8283175518,\n" +
            "      \"department\" : {\n" +
            "        \"name\" : \"VVKM\",\n" +
            "        \"id\" : 56165582,\n" +
            "        \"employee\" : {\n" +
            "          \"salary\" : 8000.0,\n" +
            "          \"ename\" : \"KOMOL\",\n" +
            "          \"eid\" : 2389\n" +
            "        }\n" +
            "      },\n" +
            "      \"employee\" : {\n" +
            "        \"ename\" : \"Russel\",\n" +
            "        \"eid\" : 5258\n" +
            "      }\n" +
            "    },\n" +
            "    \"employee\" : {\n" +
            "      \"ename\" : \"Russel\",\n" +
            "      \"eid\" : 5258\n" +
            "    }\n" +
            "  },\n" +
            "  \"department\" : {\n" +
            "    \"name\" : \"ICT\",\n" +
            "    \"id\" : 98798079087,\n" +
            "    \"department\" : {\n" +
            "      \"name\" : \"RGV\",\n" +
            "      \"id\" : 98457984,\n" +
            "      \"department\" : {\n" +
            "        \"name\" : \"MCE\",\n" +
            "        \"id\" : 94504975049,\n" +
            "        \"employee\" : {\n" +
            "          \"salary\" : 52000.0,\n" +
            "          \"ename\" : \"Russel\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"employee\" : {\n" +
            "        \"salary\" : 52000.0,\n" +
            "        \"ename\" : \"Russel\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"employee\" : {\n" +
            "      \"salary\" : 52000.0,\n" +
            "      \"ename\" : \"Russel\",\n" +
            "      \"eid\" : 5258\n" +
            "    }\n" +
            "  }\n" +
            "}");

        System.out.println(jsonObject.equals(js2));
    }
}
