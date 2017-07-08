package elasta.composer;

/**
 * Created by sohan on 5/12/2017.
 */
public interface States {
    String start = "start";
    String authorize = "authorize";
    String validate = "validate";
    String add = "add";
    String update = "update";
    String delete = "delete";
    String end = "end";
    String generateId = "generateId";
    String conversionToCriteria = "conversionToCriteria";
    String findOne = "findOne";
    String findAll = "findAll";
    String broadcast = "broadcast";
    String generateResponse = "generateResponse";
    String authorizeAll = "authorizeAll";
    String deleteAll = "deleteAll";
    String validateAll = "validateAll";
    String addAll = "addAll";
    String updateAll = "updateAll";
    String broadcastAll = "broadcastAll";
    String loadParent = "loadParent";
    String generateIdsAll = "generateIdsAll";
    String beforeAddAll = "beforeAddAll";
    String beforeAdd = "beforeAdd";
    String beforeUpdate = "beforeUpdate";
    String beforeUpdateAll = "beforeUpdateAll";
}
