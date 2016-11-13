package elasta.composer.module_exporter;

/**
 * Created by Jango on 11/13/2016.
 */
public interface States {
    String START = "START".toLowerCase();
    String INITIAL_TRANSFORMATION = "INITIAL_TRANSFORMATION".toLowerCase();
    String CONVERTION = "CONVERTION".toLowerCase();
    String CONVERTION_ERROR = "CONVERTION_ERROR".toLowerCase();
    String CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION = "CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION".toLowerCase();
    String VALIDATION = "VALIDATION".toLowerCase();
    String VALIDATION_ERROR = "VALIDATION_ERROR".toLowerCase();
    String END = "END".toLowerCase();
    String TRANSFORMATION = "TRANSFORMATION";
    String ACTION = "ACTION";
    String RESULT_PREPERATION = "RESULT_PREPERATION";
}
