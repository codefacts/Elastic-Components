package tracker.server.generators.response;

/**
 * Created by sohan on 7/4/2017.
 */
public interface DeleteAllHttpResponseGenerator extends HttpResponseGenerator<Object> {
    @Override
    HttpResponse generate(Object value);
}
