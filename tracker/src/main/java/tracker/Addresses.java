package tracker;

/**
 * Created by sohan on 6/29/2017.
 */
public interface Addresses {
    String userCreate = "user.create";
    String userFindOne = "user.find.one";

    static String post(String userCreate) {
        return "post." + userCreate;
    }
}
