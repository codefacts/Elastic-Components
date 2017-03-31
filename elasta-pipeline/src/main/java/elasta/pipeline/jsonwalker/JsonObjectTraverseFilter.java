package elasta.pipeline.jsonwalker;

/**
 * Created by sohan on 3/27/2017.
 */
public interface JsonObjectTraverseFilter {

    boolean filter(JsonObjectTraverseFunction.Params params);
}
