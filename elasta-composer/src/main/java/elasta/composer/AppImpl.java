package elasta.composer;

import elasta.vertxutils.VertxUtils;
import elasta.webutils.WebUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

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

        router.get("/api/users").handler(ctx -> {

            JsonObject jsonReq = new JsonObject()
                .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()))
                .put(ReqCnst.BODY, ctx.getBodyAsJson());

            vertxUtils.sendAndReceiveJsonObject("/users/find-all", jsonReq)
                .then(val -> ctx.response().end(val.encode()))
            ;

        });

        vertx.createHttpServer().requestHandler(router::accept).listen(6500);
        System.out.println("started");
    }

    private static void registerEventHandlers(Vertx vertx, App app) {
        vertx.eventBus().consumer("/users/find-all", new FindAllHandler(app)::findAll);
    }
}
