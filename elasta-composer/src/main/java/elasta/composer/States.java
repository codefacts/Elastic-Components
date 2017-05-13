package elasta.composer;

/**
 * Created by sohan on 5/12/2017.
 */
public interface States {
    String start = "start";
    String authorization = "authorization";
    String validation = "validation";
    String insert = "insert";
    String update = "update";
    String delete = "delete";
    String end = "end";
    String idGeneration = "idGeneration";
    String conversionToCriteria = "conversionToCriteria";
    String findOne = "findOne";
    String findAll = "findAll";
    String broadcast = "broadcast";
    String generateResponse = "generateResponse";
}
