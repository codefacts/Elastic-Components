package elasta.orm.nm.delete;

import elasta.orm.nm.delete.BelongsToDeleteHandler;

/**
 * Created by Jango on 17/02/07.
 */
final public class BelongsToDeleteDependency {
    private BelongsToDeleteHandler deleteHandler;
    private String fieldName;

    public BelongsToDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(BelongsToDeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
