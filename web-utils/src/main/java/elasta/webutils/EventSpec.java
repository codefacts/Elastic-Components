package elasta.webutils;

/**
 * Created by Jango on 9/12/2016.
 */
public class EventSpec {
    final String address;
    final EventHandler handler;

    public EventSpec(String address, EventHandler handler) {
        this.address = address;
        this.handler = handler;
    }

    public String getAddress() {
        return address;
    }

    public EventHandler getHandler() {
        return handler;
    }
}
