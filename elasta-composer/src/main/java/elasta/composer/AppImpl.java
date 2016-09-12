package elasta.composer;

import elasta.vertxutils.VertxUtils;
import elasta.webutils.WebUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by Jango on 9/11/2016.
 */
public class AppImpl implements App {

    public final WebUtils webUtils;
    public final VertxUtils vertxUtils;

    public AppImpl(WebUtils webUtils, VertxUtils vertxUtils) {
        this.webUtils = webUtils;
        this.vertxUtils = vertxUtils;
    }

    @Override
    public WebUtils webUtils() {
        return webUtils;
    }

    @Override
    public VertxUtils vertxUtils() {
        return vertxUtils;
    }

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();

        final App app = App.app(vertx);
        final WebUtils webUtils = app.webUtils();
        final VertxUtils vertxUtils = app.vertxUtils();

        registerEventHandlers(vertx, app);

        Router router = Router.router(vertx);

        registerFilters(router);

        router.get("/api/users").handler(ctx -> {

            JsonObject jsonReq = new JsonObject()
                .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()))
                .put(ReqCnst.BODY, ctx.getBodyAsJson());

            vertxUtils.sendAndReceiveJsonObject("/users/find-all", jsonReq)
                .then(val -> ctx.response().end(val.encode()))
            ;

        });

        router.post("/api/users").handler(BodyHandler.create());
        router.post("/api/users").handler(ctx -> {

            JsonObject jsonReq = new JsonObject()
                .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()))
                .put(ReqCnst.BODY, ctx.getBodyAsJson());

            vertxUtils.sendAndReceiveJsonObject("/users/create", jsonReq)
                .then(val -> ctx.response().end(val.encode()))
            ;

        });

        router.put("/api/users/:id").handler(BodyHandler.create());
        router.put("/api/users/:id").handler(ctx -> {

            JsonObject jsonReq = new JsonObject()
                .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()))
                .put(ReqCnst.BODY, ctx.getBodyAsJson());

            vertxUtils.sendAndReceiveJsonObject("/users/update-all-properties", jsonReq)
                .then(val -> ctx.response().end(val.encode()))
            ;

        });

        router.patch("/api/users/:id").handler(BodyHandler.create());
        router.patch("/api/users/:id").handler(ctx -> {

            JsonObject jsonReq = new JsonObject()
                .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()))
                .put(ReqCnst.BODY, ctx.getBodyAsJson());

            vertxUtils.sendAndReceiveJsonObject("/users/update-some-properties", jsonReq)
                .then(val -> ctx.response().end(val.encode()))
            ;

        });

        router.delete("/api/users/:id").handler(BodyHandler.create());
        router.delete("/api/users/:id").handler(ctx -> {

            JsonObject jsonReq = new JsonObject()
                .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()))
                .put(ReqCnst.BODY, ctx.getBodyAsJson());

            vertxUtils.sendAndReceiveJsonObject("/users/delete", jsonReq)
                .then(val -> ctx.response().end(val.encode()))
            ;

        });

        router.get("/api/users/:id").handler(BodyHandler.create());
        router.get("/api/users/:id").handler(ctx -> {

            JsonObject jsonReq = new JsonObject()
                .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()))
                .put(ReqCnst.BODY, ctx.getBodyAsJson());

            vertxUtils.sendAndReceiveJsonObject("/users/find", jsonReq)
                .then(val -> ctx.response().end(val.encode()))
            ;

        });

        vertx.createHttpServer().requestHandler(router::accept).listen(6500);
        System.out.println("started");
    }

    private static void registerFilters(Router router) {
        router.route("/api/*").handler(event -> {
            event.response().putHeader("Content-Type", "application/json");
            event.next();
        });
    }

    private static void registerEventHandlers(Vertx vertx, App app) {
        vertx.eventBus().consumer("/users/find-all", new FindAllHandler(app)::findAll);
        vertx.eventBus().consumer("/users/find", new FindHandler(app)::find);
        vertx.eventBus().consumer("/users/create", new CreateHandler(app)::create);
        vertx.eventBus().consumer("/users/update-all-properties", new UpdateAllPropertiesHandler(app)::updateAllProperties);
        vertx.eventBus().consumer("/users/update-some-properties", new UpdateSomePropertiesHandler(app)::updateSomeProperties);
        vertx.eventBus().consumer("/users/delete", new DeleteHandler(app)::delete);
    }
}
