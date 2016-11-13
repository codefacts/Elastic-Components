package elasta.composer;

/**
 * Created by Jango on 11/13/2016.
 */
final public class Resource {
    private final String name;
    private final String displayName;
    private final String pluralName;
    private final String eventPath;

    Resource(String name, String displayName, String pluralName, String eventPath) {
        this.name = name;
        this.displayName = displayName;
        this.pluralName = pluralName;
        this.eventPath = eventPath;
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
}
