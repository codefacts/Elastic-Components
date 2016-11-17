package elasta.composer;

/**
 * Created by Jango on 11/13/2016.
 */
final public class Resource {
    private final String name;
    private final String displayName;
    private final String pluralName;
    private final String eventPath;
    private final String entity;

    Resource(String name, String displayName, String pluralName, String eventPath, String entity) {
        this.name = name;
        this.displayName = displayName;
        this.pluralName = pluralName;
        this.eventPath = eventPath;
        this.entity = entity;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPluralName() {
        return pluralName;
    }

    public String getEventPath() {
        return eventPath;
    }

    public String getEntity() {
        return entity;
    }
}
