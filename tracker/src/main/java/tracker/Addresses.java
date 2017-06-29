package tracker;

/**
 * Created by sohan on 6/29/2017.
 */
public interface Addresses {
    String userCreate = "user.create";

    static String post(String userCreate) {
        return "post." + userCreate;
    }
}
