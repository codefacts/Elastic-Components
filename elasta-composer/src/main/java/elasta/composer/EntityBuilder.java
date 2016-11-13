package elasta.composer;

public class EntityBuilder {
    private String name;
    private String displayName;
    private String pluralName;
    private String eventPath;

    public EntityBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EntityBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public EntityBuilder setPluralName(String pluralName) {
        this.pluralName = pluralName;
        return this;
    }

    public EntityBuilder setEventPath(String eventPath) {
        this.eventPath = eventPath;
        return this;
    }

    public Entity createEntity() {
        return new Entity(name, displayName, pluralName, eventPath);
    }
}