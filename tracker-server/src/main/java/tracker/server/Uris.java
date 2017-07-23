package tracker.server;

/**
 * Created by sohan on 7/1/2017.
 */
public interface Uris {
    String loginUri = "/login";
    String logoutUri = "/logout";
    String userUri = "/users";
    String deviceUri = "/devices";
    String positionUri = "/positions";
    String groupByUserId = "/group-by-user-id";
    String outletUri = "/outlets";
    String uploadUri = "/uploads";
    String androidTrackersPicturesUri = "/android-trackers/pictures";
    String outletsPicturesUri = "/outlets/pictures";
    String resourcesUri = "/resources";
    String publicUri = "/public";

    static String api(String uriPart) {
        return "/api" + uriPart;
    }

    static String upload(String uriPart) {
        return "/uploads" + uriPart;
    }

    static String singularUri(String uri) {
        return uri + "/:" + PathParams.id;
    }

    static String bulkUri(String uri) {
        return uri + "/bulk";
    }
}
