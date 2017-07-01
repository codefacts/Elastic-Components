package tracker.message.handlers.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.composer.model.response.ErrorModel;
import elasta.orm.Orm;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.JsonOps;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import tracker.AppUtils;
import tracker.StatusCodes;
import tracker.entity_config.Entities;
import tracker.message.handlers.AuthenticateMessageHandler;
import tracker.message.handlers.ex.AuthenticateMessageHandlerException;
import tracker.model.AuthenticationSuccessModel;
import tracker.model.UserModel;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 7/1/2017.
 */
final public class AuthenticateMessageHandlerImpl implements AuthenticateMessageHandler {
    final Orm orm;

    public AuthenticateMessageHandlerImpl(Orm orm) {
        Objects.requireNonNull(orm);
        this.orm = orm;
    }

    @Override
    public void handle(Message<JsonObject> message) {

        JsonObject user = message.body();

        final String password = user.getString(UserModel.password);

        HashMap<String, Object> map = new HashMap<>(user.getMap());

        map.remove(UserModel.password);

        final List<JsonObject> conditions = map.entrySet().stream()
            .map(entry -> JsonOps.eq("r." + entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        if (conditions.isEmpty()) {
            throw new AuthenticateMessageHandlerException("Request Body does not contains enough data");
        }

        orm
            .findOne(
                Entities.USER,
                "r",
                conditions.size() == 1 ? conditions.get(0) : JsonOps.and(conditions),
                ImmutableList.of(
                    new FieldExpressionImpl(PathExpression.create("r", UserModel.userId)),
                    new FieldExpressionImpl(PathExpression.create("r", UserModel.username)),
                    new FieldExpressionImpl(PathExpression.create("r", UserModel.password))
                )
            )
            .then(foundUser -> {

                if (Utils.not(Objects.equals(foundUser.getString(UserModel.password), password))) {
                    message.reply(
                        error(StatusCodes.passwordMismatchError, "Password does not match")
                    );
                }

                message.reply(
                    new JsonObject(
                        ImmutableMap.of(
                            AuthenticationSuccessModel.userId, foundUser.getString(UserModel.userId),
                            AuthenticationSuccessModel.username, foundUser.getString(UserModel.username)
                        )
                    )
                );
            })
            .err(throwable -> message.reply(
                error(StatusCodes.userNotFoundError, "User not found")
            ))
        ;
    }

    private JsonObject error(String statusCode, String message) {
        return new JsonObject(
            ImmutableMap.of(
                ErrorModel.statusCode, statusCode,
                ErrorModel.message, message
            )
        );
    }
}
