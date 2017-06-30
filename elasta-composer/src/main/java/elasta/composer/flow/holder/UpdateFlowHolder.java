package elasta.composer.flow.holder;

import elasta.core.flow.Flow;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
@Value
@Builder
final public class UpdateFlowHolder {
    final Flow flow;

    public UpdateFlowHolder(Flow flow) {
        Objects.requireNonNull(flow);
        this.flow = flow;
    }
}
