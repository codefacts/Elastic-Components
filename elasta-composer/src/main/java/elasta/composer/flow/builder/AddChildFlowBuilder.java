package elasta.composer.flow.builder;

import elasta.core.flow.Flow;

/**
 * Created by sohan on 5/24/2017.
 */
public interface AddChildFlowBuilder extends FlowBuilder {
    @Override
    Flow build();
}
