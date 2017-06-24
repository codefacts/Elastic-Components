package elasta.composer;

/**
 * Created by sohan on 5/12/2017.
 */
public interface States {
    String start = "start";
    String authorize = "authorize";
    String validate = "validate";
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
    String authorizeAll = "authorizeAll";
    String deleteAll = "deleteAll";
    String validateAll = "validateAll";
    String insertAll = "insertAll";
    String updateAll = "updateAll";
    String broadcastAll = "broadcastAll";
    String loadParent = "loadParent";
}