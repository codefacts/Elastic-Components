package elasta.orm.nm.delete;

import elasta.orm.nm.delete.DirectDeleteHandler;

/**
 * Created by Jango on 17/02/07.
 */
final public class DirectDeleteDependency {
    private String fieldName;
    private DirectDeleteHandler deleteHandler;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DirectDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(DirectDeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }
}
