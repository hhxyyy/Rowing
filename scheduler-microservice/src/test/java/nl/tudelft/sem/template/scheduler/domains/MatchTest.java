package nl.tudelft.sem.template.scheduler.domains;

import nl.tudelft.sem.template.scheduler.domains.Match;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatchTest {
    @Test
    public void testConstructor() {
        Match match = new Match("user", 1, 2, true);

        assertEquals("user", match.getUserId());
        assertEquals(1, match.getEventId());
        assertEquals(2, match.getPosition());
        assertTrue(match.isPending());
    }

    @Test
    public void testEqualsInstance() {
        Match match1 = new Match("user", 1, 2, false);
        assertEquals(match1, match1);
    }

    @Test
    public void testEqualsNull() {
        Match match1 = new Match("user", 1, 2, false);
        assertNotEquals(match1, null);
    }

    @Test
    public void testEqualsOtherClass() {
        Match match1 = new Match("user", 1, 2, false);
        assertNotEquals(match1, "str");
    }

    @Test
    public void testEquals() {
        Match match1 = new Match("user", 1, 2, false);
        Match match2 = new Match("user", 1, 2, false);

        assertEquals(match1, match2);
    }

    @Test
    public void testNotEquals() {
        Match match1 = new Match(2, "user", 1, 2, true);
        Match match2 = new Match(3, "user", 1, 2, false);

        assertNotEquals(match1, match2);
    }

    @Test
    public void testHashCode() {
        Match match1 = new Match("user", 1, 2, true);
        Match match2 = new Match("user", 1, 2, true);

        assertEquals(match1.hashCode(), match2.hashCode());
    }
}

