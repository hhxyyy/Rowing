package nl.tudelft.sem.template.scheduler.domains;

import nl.tudelft.sem.template.scheduler.domains.Notification;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class NotificationTest {
    @Test
    public void testConstructor() {

        Notification notification = new Notification("owner", "user", 1, 1);

        assertEquals("owner", notification.getOwnerId());
        assertEquals("user", notification.getUserId());
        assertEquals(1, notification.getEventId());
        assertEquals(1, notification.getPosition());
    }

    @Test
    public void testEqualsInstances() {
        Notification notification1 = new Notification("owner", "user", 1, 1);
        assertEquals(notification1, notification1);
    }

    @Test
    public void testEqualsNull() {
        Notification notification1 = new Notification("owner", "user", 1, 1);
        assertNotEquals(notification1, null);
    }

    @Test
    public void testEqualsOtherClass() {
        Notification notification1 = new Notification("owner", "user", 1, 1);
        assertNotEquals(notification1, "class");
    }

    @Test
    public void testEquals() {
        Notification notification1 = new Notification("owner", "user", 1, 1);
        Notification notification2 = new Notification("owner", "user", 1, 1);

        assertEquals(notification1, notification2);
    }

    @Test
    public void testNotEquals() {
        Notification notification1 = new Notification(2, "owner", "user", 1, 1);
        Notification notification2 = new Notification(3, "owneerrer", "user", 1, 1);

        assertNotEquals(notification1, notification2);
    }

    @Test
    public void testHashCode() {
        Notification notification1 = new Notification("owner", "user", 1, 1);
        Notification notification2 = new Notification("23", "43", 4, 0);
        assertEquals(notification1.hashCode(), notification2.hashCode());
    }
}
