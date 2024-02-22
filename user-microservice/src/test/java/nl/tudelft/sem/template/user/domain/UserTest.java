package nl.tudelft.sem.template.user.domain;

import nl.tudelft.sem.template.user.domain.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void simpleTestEquals() {
        User user = new User("lexi", "C4", "F", "TU Delft", true);
        assertEquals(user, new User("lexi", "C4", "F", "TU Delft", true));
        assertNotEquals(user, new User("lexi", "C4", "M", "X", false));
    }

    @Test
    public void complexTestEquals() {

        User user = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user, user);
    }

    @Test
    public void complexTestNotEquals1() {
        User user = new User("lexi", "C4", "F", "TU Delft", true);

        assertNotEquals(user, 3);
    }

    @Test
    public void complexTestNotEquals2() {
        User user = new User("lexi", "C4", "F", "TU Delft", true);

        assertFalse(user.equals(null));
    }

    @Test
    public void complexTestNotEquals3() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        User user2 = new User("zoya", "C4", "F", "TU Delft", true);

        assertNotEquals(user1, user2);
    }

    @Test
    public void complexTestNotEquals4() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        User user2 = new User("lexi", "8+", "F", "TU Delft", true);

        assertNotEquals(user1, user2);
    }

    @Test
    public void complexTestNotEquals5() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        User user2 = new User("lexi", "C4", "M", "TU Delft", true);

        assertNotEquals(user1, user2);
    }

    @Test
    public void complexTestNotEquals6() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        User user2 = new User("lexi", "C4", "F", "X", true);

        assertNotEquals(user1, user2);
    }

    @Test
    public void complexTestNotEquals7() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        User user2 = new User("lexi", "C4", "F", "TU Delft", false);

        assertNotEquals(user1, user2);
    }

    @Test
    public void simpleTestHash1() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        User user2 = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user1.hashCode(), user2.hashCode());

    }

    @Test
    public void simpleTestHash2() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        User user3 = new User("lexi", "4+", "F", "TU Delft", true);

        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    public void simpleTestConstructor() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user1.getUsername(), "lexi");
        assertEquals(user1.getCertificate(), "C4");
        assertEquals(user1.getGender(), "F");
        assertEquals(user1.getOrganization(), "TU Delft");
        assertEquals(user1.isUserProfessional(), true);

    }

    @Test
    void getUsername() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user1.getUsername(), "lexi");

    }

    @Test
    void getCertificate() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user1.getCertificate(), "C4");

    }

    @Test
    void getGender() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user1.getGender(), "F");

    }

    @Test
    void getOrganization() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user1.getOrganization(), "TU Delft");

    }

    @Test
    void isProfessional() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);

        assertEquals(user1.isUserProfessional(), true);

    }

    @Test
    void setUsername() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        user1.setUsername("zoya");
        assertEquals(user1.getUsername(), "zoya");

    }

    @Test
    void setCertificate() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        user1.setCertificate("8+");
        assertEquals(user1.getCertificate(), "8+");

    }

    @Test
    void setGender() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        user1.setGender("M");
        assertEquals(user1.getGender(), "M");

    }

    @Test
    void setOrganization() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        user1.setOrganization("TU Delft");
        assertEquals(user1.getOrganization(), "TU Delft");

    }

    @Test
    void setProfessional() {

        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        user1.setProfessional(false);
        assertEquals(user1.isUserProfessional(), false);

    }

    @Test
    void testToString() {
        User user1 = new User("lexi", "C4", "F", "TU Delft", true);
        assertEquals(user1.toString(),
                "User(username=lexi, certificate=C4, gender=F,"
                        + " organization=TU Delft, isProfessional=true)");
    }
}
