package nl.tudelft.sem.template.user.models;


public class EventIdModel {
    private long eventId;
    private int position;

    public EventIdModel() {
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isValid() {
        return eventId >= 1 && position >= 0 && position <= 4;
    }
}
