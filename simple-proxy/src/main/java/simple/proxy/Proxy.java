package simple.proxy;

import com.google.common.collect.ImmutableList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

/**
 * Created by sohan on 2017-08-05.
 */
final public class Proxy extends AbstractVerticle {
    private static final Logger log = LogManager.getLogger(Proxy.class);
    private final String targetScheme;
    private final String targetHost;
    private final int targetPort;
    private final String targetPrefix;
    private final String uri;
    private final int port;

    public Proxy(String targetScheme, String targetHost, int targetPort, String targetPrefix, String uri, int port) {
        Objects.requireNonNull(targetScheme);
        Objects.requireNonNull(targetHost);
        Objects.requireNonNull(targetPort);
        Objects.requireNonNull(targetPrefix);
        Objects.requireNonNull(uri);
        Objects.requireNonNull(port);
        this.targetScheme = targetScheme;
        this.targetHost = targetHost;
        this.targetPort = targetPort;
        this.targetPrefix = targetPrefix;
        this.uri = uri;
        this.port = port;
    }

    @Override
    public void start() throws Exception {

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        createRouter(router);

        server.requestHandler(router::accept);
        server.listen(port);
        System.out.println("simple.proxy.Proxy Running at port: " + port);
    }

    private void createRouter(Router router) {

        final HttpClient httpClient = vertx.createHttpClient(
            new HttpClientOptions()
                .setDefaultHost(targetHost)
                .setDefaultPort(targetPort)
        );

        final BodyHandler bodyHandler = BodyHandler.create().setBodyLimit(10 * 1024 * 1024);

        router.post(uri).handler(bodyHandler);
        router.post(uri).handler(postHandler(httpClient, HttpMethod.POST));

        router.put(uri).handler(bodyHandler);
        router.put(uri).handler(postHandler(httpClient, HttpMethod.PUT));

        router.patch(uri).handler(bodyHandler);
        router.patch(uri).handler(postHandler(httpClient, HttpMethod.PATCH));

        router.delete(uri).handler(bodyHandler);
        router.delete(uri).handler(postHandler(httpClient, HttpMethod.DELETE));

        router.get(uri).handler(postHandler(httpClient, HttpMethod.GET));
    }

    private Handler<RoutingContext> postHandler(HttpClient httpClient, HttpMethod method) {
        return ctx -> {

            if (ctx.request().isExpectMultipart()) {
                ImmutableList.Builder<String> listBuilder = ImmutableList.builder();
                ctx.fileUploads().forEach(fileUpload -> {
                    final String toPath = "D:\\visual-studio-projects\\MericoServer\\MericoServer\\Content\\outlets\\" + fileUpload.fileName();
                    if (vertx.fileSystem().existsBlocking(toPath)) {
                        vertx.fileSystem().deleteBlocking(toPath);
                    }
                    vertx.fileSystem().moveBlocking(
                        fileUpload.uploadedFileName(),
                        toPath
                    );
                    listBuilder.add(baseUri(ctx.request(), "/Content/outlets/" + fileUpload.fileName()));
                });
                ctx.response().end(new JsonArray(listBuilder.build()).encodePrettily());
                return;
            }

            final Buffer requestBody = ctx.getBody();

            System.out.println("### received request from: " + ctx.request().absoluteURI());

            String targetUri = targetServerUri(ctx.request().uri());

            System.out.println("### httpRequest: " + targetUri);

            HttpClientRequest httpRequest = httpClient.request(method, targetUri);

            httpRequest.headers().addAll(ctx.request().headers());

            System.out.println("## Request Headers: " + ctx.request().headers());
            System.out.println("## Request Body: " + printBuffer(requestBody));

            httpRequest
                .handler(response -> {
                    response
                        .exceptionHandler(throwable -> {
                            log.error("Error getting response from " + method + ": " + targetUri, throwable);
                            ctx.fail(throwable);
                        })
                        .bodyHandler(buffer -> {
                            ctx.response().headers().addAll(response.headers());
                            ctx.response().setStatusCode(response.statusCode());
                            ctx.response().setStatusMessage(response.statusMessage());
                            ctx.response().end(buffer);
                            System.out.println("#### Response StatusCode: " + response.statusCode());
                            System.out.println("#### Response StatusMessage: " + response.statusMessage());
                            System.out.println("#### Request Headers: " + response.headers());
                            System.out.println("#### Response Body: " + printBuffer(buffer));
                            System.out.println("---------------------------------------------Respond received-----------------------------------------");
                        })
                    ;
                })
                .exceptionHandler(throwable -> {
                    log.error("Error making " + method + ": " + targetUri, throwable);
                    ctx.fail(throwable);
                })
                .end(requestBody == null ? Buffer.buffer() : requestBody);
            ;
        };
    }

    private String baseUri(HttpServerRequest request, String path) {
        return request.scheme() + "://" + request.host() + path;
    }

    private String printBuffer(Buffer buffer) {
        try {
            String str = buffer.toString();
            try {
                return new JsonObject(str).encodePrettily();
            } catch (Exception e) {
                return str;
            }
        } catch (Exception e) {
            return "Buffer is not readable";
        }
    }

    private String targetServerUri(String uri) {
        return targetScheme + "://" + targetHost + ":" + targetPort + targetPrefix + uri;
    }

    public static void main(String[] args) {
        final JsonObject config = loadConfig("proxy-config.json");
        Vertx.vertx().deployVerticle(new Proxy(
            config.getString("targetScheme"),
            config.getString("targetHost"),
            config.getInteger("targetPort"),
            config.getString("targetPrefix"),
            config.getString("uri"),
            config.getInteger("port")
        ));
    }

    private static JsonObject loadConfig(String fileName) {
        final File file = new File(
            new File("").getAbsoluteFile(),
            fileName
        );

        try {
            return new JsonObject(
                new String(
                    Files.readAllBytes(file.toPath()),
                    StandardCharsets.UTF_8
                )
            );
        } catch (IOException e) {
            throw new InvalidConfigurationException(e);
        }
    }
}
