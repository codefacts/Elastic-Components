package elasta.composer;

public class ResourceBuilder {
    private String name;
    private String displayName;
    private String pluralName;
    private String eventPath;
    private String entity;

    public ResourceBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ResourceBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ResourceBuilder setPluralName(String pluralName) {
        this.pluralName = pluralName;
        return this;
    }

    public ResourceBuilder setEventPath(String eventPath) {
        this.eventPath = eventPath;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getEntity() {
        return entity;
    }

    public ResourceBuilder setEntity(String entity) {
        this.entity = entity;
        return this;
    }

    public Resource build() {
        return new Resource(name, displayName, pluralName, eventPath, entity);
    }
}