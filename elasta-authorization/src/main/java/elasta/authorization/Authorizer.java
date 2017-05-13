package elasta.authorization;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
@FunctionalInterface
public interface Authorizer {

    boolean authorize(AuthorizeParams params);

    @Value
    @Builder
    final class AuthorizeParams<UserId, Request> {
        final String action;
        final UserId userId;
        final Request request;

        AuthorizeParams(String action, UserId userId, Request request) {
            Objects.requireNonNull(action);
            Objects.requireNonNull(userId);
            Objects.requireNonNull(request);
            this.action = action;
            this.userId = userId;
            this.request = request;
        }
    }
}
