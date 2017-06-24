package elasta.webutils.module.exporter;

import elasta.module.MutableModuleSystem;
import elasta.webutils.*;
import elasta.webutils.impl.*;
import elasta.webutils.query.string.QueryStringToJsonObjectConverter;
import elasta.webutils.impl.JsonObjectRequestConverterImpl;
import elasta.webutils.query.string.impl.QueryStringToJsonObjectConverterImpl;

/**
 * Created by Jango on 11/9/2016.
 */
final public class WebUtilsExporterImpl implements WebUtilsExporter {
    @Override
    public void exportTo(MutableModuleSystem mutableModuleSystem) {


        mutableModuleSystem.export(RequestConverter.class, module -> module.export(
            new JsonObjectRequestConverterImpl()
        ));

        mutableModuleSystem.export(QueryStringToJsonObjectConverter.class, module -> module.export(
            new QueryStringToJsonObjectConverterImpl()
        ));

        mutableModuleSystem.export(ResponseGenerator.class, module -> module.export(new JsonObjectResponseGeneratorImpl()));
        
    }
}
