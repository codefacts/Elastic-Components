package elasta.composer.module_exporter;

/**
 * Created by Jango on 11/13/2016.
 */
public interface DbEvents {
    String DB = "DB".toLowerCase();
    String DB_CREATE = "DB.CREATE".toLowerCase();
    String DB_UPDATE = "DB.UPDATE".toLowerCase();
    String DB_DELETE = "DB.DELETE".toLowerCase();
    String DB_FIND = "DB.FIND".toLowerCase();
    String DB_FIND_BY_PARAMS = "DB.FIND.BY.PARAMS".toLowerCase();
    String DB_FIND_ALL = "DB.FIND.ALL".toLowerCase();
}
