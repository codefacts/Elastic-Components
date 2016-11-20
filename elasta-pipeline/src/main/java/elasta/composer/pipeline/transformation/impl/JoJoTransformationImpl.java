package elasta.composer.pipeline.transformation.impl;

import elasta.composer.pipeline.transformation.JoJoTransformation;
import elasta.composer.pipeline.transformation.Transformation;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/1/16.
 */
public class JoJoTransformationImpl implements JoJoTransformation {
    private final List<JoJoTransformation> transformList;

    public JoJoTransformationImpl(List<JoJoTransformation> transformList) {
        this.transformList = transformList;
    }

    @Override
    public JsonObject transform(JsonObject val) {
        for (Transformation<JsonObject, JsonObject> transformation : transformList) {
            val = transformation.transform(val);
        }
        return val;
    }
}
