package tracker.server.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tracker.StatusCodes;
import tracker.server.ex.RequestException;
import tracker.server.request.handlers.RequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 7/22/2017.
 */
final public class FileUploadRequestHandlerImpl implements RequestHandler {
    static final Logger log = LogManager.getLogger(FileUploadRequestHandlerImpl.class);
    private final Vertx vertx;
    private final RequestProcessingErrorHandler requestProcessingErrorHandler;
    private final Map<String, String> uriToUploadDirectorMap;
    private final String baseUploadDir;
    private final String staticResourcesUri;

    public FileUploadRequestHandlerImpl(Vertx vertx, RequestProcessingErrorHandler requestProcessingErrorHandler, Map<String, String> uriToUploadDirectorMap, String baseUploadDir, String staticResourcesUri) {
        Objects.requireNonNull(vertx);
        Objects.requireNonNull(requestProcessingErrorHandler);
        Objects.requireNonNull(uriToUploadDirectorMap);
        Objects.requireNonNull(baseUploadDir);
        Objects.requireNonNull(staticResourcesUri);
        this.vertx = vertx;
        this.requestProcessingErrorHandler = requestProcessingErrorHandler;
        this.uriToUploadDirectorMap = uriToUploadDirectorMap;
        this.baseUploadDir = baseUploadDir;
        this.staticResourcesUri = staticResourcesUri;
    }

    @Override
    public void handle(RoutingContext ctx) {

        final HttpServerRequest request = ctx.request();

        final String uploadDir = uriToUploadDirectorMap.get(request.uri());

        Objects.requireNonNull(uploadDir);

        createDirsIfNotExists(uploadDir);

        Promises
            .when(
                ctx.fileUploads().stream()
                    .map(fileUpload -> uploadNonBlocking(fileUpload.uploadedFileName(), uploadDir + "/" + fileUpload.fileName()))
                    .collect(Collectors.toList())
            )
            .then(strings -> ctx.response().end(
                new JsonArray(
                    strings.stream()
                        .map(fullPath -> resolveUri(ctx.request().absoluteURI(), fullPath))
                        .collect(Collectors.toList())
                ).toString())
            )
            .err(throwable -> requestProcessingErrorHandler.handleError(throwable, ctx))
        ;
    }

    private Promise<String> uploadNonBlocking(String from, String to) {

        File file = new File(to);

        if (file.exists()) {
            file.delete();
        }

        final Defer<String> defer = Promises.defer();

        System.out.println("uploading to: " + to);

        vertx.fileSystem()
            .move(from, to, makeDeferred(defer, to));

        return defer.promise();
    }

    private Handler<AsyncResult<Void>> makeDeferred(Defer<String> defer, String to) {

        return event -> {

            if (event.failed()) {
                defer.reject(event.cause());
                return;
            }

            defer.resolve(to);
        };
    }

    private String resolveUri(String absoluteUri, String fullFilePath) {

        return baseUri(absoluteUri) + staticResourcesUri + fullFilePath.substring(baseUploadDir.length()).replace('\\', '/');
    }

    private String baseUri(String absoluteUri) {
        URI uri = null;
        try {

            uri = new URI(absoluteUri);

            return uri.getScheme() + "://" + uri.getHost() + (uri.getPort() == 80 ? "" : (":" + uri.getPort()));

        } catch (URISyntaxException e) {
            throw new RequestException(StatusCodes.unexpectedError, "Unexpected error processing request");
        }
    }

    private void createDirsIfNotExists(String uploadDir) {
        File file = new File(uploadDir);
        if (file.exists()) {
            return;
        }
        file.mkdirs();
    }
}
