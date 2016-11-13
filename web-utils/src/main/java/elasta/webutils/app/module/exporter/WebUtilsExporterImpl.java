package elasta.webutils.app.module.exporter;

import elasta.core.eventbus.SimpleEventBus;
import elasta.module.ModuleSystem;
import elasta.webutils.app.*;
import elasta.webutils.app.impl.RequestConverterImpl;
import elasta.webutils.app.impl.RequestHandlerImpl;
import elasta.webutils.app.impl.ResponseGeneratorImpl;

/**
 * Created by Jango on 11/9/2016.
 */
public class WebUtilsExporterImpl implements WebUtilsExporter {
    @Override
    public void exportTo(ModuleSystem moduleSystem) {

        moduleSystem.export(RequestHandler.class, module -> module.export(new RequestHandlerImpl(

            module.require(RequestConverter.class),
            module.require(UriToEventTranslator.class),
            module.require(ResponseGenerator.class),
            module.require(SimpleEventBus.class)
        )));

        moduleSystem.export(RequestConverter.class, module -> module.export(new RequestConverterImpl()));

        moduleSystem.export(ResponseGenerator.class, module -> module.export(new ResponseGeneratorImpl()));

        moduleSystem.export(DefaultValues.class, module -> module.export(new DefaultValuesImpl()));
    }
}
