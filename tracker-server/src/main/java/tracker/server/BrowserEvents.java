package tracker.server;

import io.vertx.ext.web.handler.sockjs.PermittedOptions;

/**
 * Created by sohan on 7/11/2017.
 */
public interface BrowserEvents {
    String userPositionTracking = "user-position-tracking";
    String replayUserPositions = "replay-user-positions";
}
