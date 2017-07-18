package tracker.message.handlers.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.composer.model.response.ErrorModel;
import elasta.orm.Orm;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.JsonOps;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import tracker.StatusCodes;
import tracker.entity_config.Entities;
import tracker.message.handlers.AuthenticateMessageHandler;
import tracker.message.handlers.ex.AuthenticateMessageHandlerException;
import tracker.model.AuthSuccessModel;
import tracker.model.UserModel;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 7/1/2017.
 */
final public class AuthenticateMessageHandlerImpl implements AuthenticateMessageHandler {
    final Orm orm;
    final Set<String> loginFields;
    final String userKey;
    final String passwordKey;

    public AuthenticateMessageHandlerImpl(Orm orm, Set<String> loginFields, String userKey, String passwordKey) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(loginFields);
        Objects.requireNonNull(userKey);
        Objects.requireNonNull(passwordKey);
        this.orm = orm;
        this.loginFields = ImmutableSet.copyOf(loginFields);
        this.userKey = userKey;
        this.passwordKey = passwordKey;
        if (loginFields.isEmpty()) {
            throw new AuthenticateMessageHandlerException("LoginFields can not be empty");
        }
    }

    @Override
    public void handle(Message<JsonObject> message) {

        JsonObject user = message.body();

        Objects.requireNonNull(user);

        final String username = user.getString(userKey);
        final String password = user.getString(passwordKey);

        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        final JsonObject criteria;
        {
            if (loginFields.size() == 1) {

                criteria = JsonOps.eq("r." + loginFields.stream().findAny().get(), username);

            } else {

                criteria = JsonOps.or(
                    loginFields.stream().map(field -> JsonOps.eq("r." + field, username)).collect(Collectors.toList())
                );

            }
        }

        orm
            .findOne(
                Entities.USER_ENTITY,
                "r",
                criteria,
                ImmutableList.of(
                    new FieldExpressionImpl(PathExpression.create("r", UserModel.userId)),
                    new FieldExpressionImpl(PathExpression.create("r", UserModel.username)),
                    new FieldExpressionImpl(PathExpression.create("r", UserModel.password))
                )
            )
            .then(foundUser -> {

                if (not(Objects.equals(foundUser.getString(UserModel.password), password))) {
                    message.reply(
                        error(StatusCodes.passwordMismatchError, "Password does not match")
                    );
                }

                message.reply(
                    new JsonObject(
                        ImmutableMap.of(
                            AuthSuccessModel.userId, foundUser.getString(UserModel.userId),
                            AuthSuccessModel.username, foundUser.getString(UserModel.username)
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
