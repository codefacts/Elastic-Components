package elasta.composer.module_exporter;

import elasta.core.flow.ExitEventHandlerP;

/**
 * Created by Jango on 11/13/2016.
 */
public interface FLowEnterHandlers {
    String START = "START".toLowerCase();
    String INITIAL_TRANSFORMATION = "INITIAL_TRANSFORMATION".toLowerCase();
    String CONVERTION = "CONVERTION".toLowerCase();
    String CONVERTION_ERROR = "CONVERTION_ERROR".toLowerCase();
    String CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION = "CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION".toLowerCase();
    String VALIDATION = "VALIDATION".toLowerCase();
    String VALIDATION_ERROR = "VALIDATION_ERROR".toLowerCase();
    String END = "END".toLowerCase();
    String TRANSFORMATION = "TRANSFORMATION".toLowerCase();
    String ACTION = "ACTION".toLowerCase();
    String RESULT_PREPERATION = "RESULT_PREPERATION".toLowerCase();
}
