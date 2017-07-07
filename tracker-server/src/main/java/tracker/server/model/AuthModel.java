package tracker.server.model;

import tracker.model.AuthSuccessModel;

/**
 * Created by sohan on 7/1/2017.
 */
public interface AuthModel extends AuthSuccessModel {
    String authToken = "authToken";
    String expireIn = "expireIn";
}
