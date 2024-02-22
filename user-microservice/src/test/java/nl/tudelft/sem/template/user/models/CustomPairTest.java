package nl.tudelft.sem.template.user.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tudelft.sem.template.user.domain.CustomPair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomPairTest {
    @Test
    void test() throws JsonProcessingException {
        CustomPair<Date, Date> d1 = new CustomPair<>(new Date(2022, 11, 27), new Date(2022, 11, 28));
        List<CustomPair<Date, Date>> availability = new ArrayList<>();
        availability.add(d1);
        boolean[] positions = {true, false, false, false, false};
        UserInformationModel u = new UserInformationModel(availability, positions);
        String jsonVersion = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(u);
        System.out.println(jsonVersion);
    }

    @Test
    void customPairTest1() {
        CustomPair<Long, String> test = new CustomPair<>(76866L, "abc");
        assertEquals(76866L, (long) test.getFirst());
        assertEquals("abc", test.getSecond());
    }

    @Test
    void customPairTest2() {
        CustomPair<Long, String> test = new CustomPair<>(10000L, "cba");
        assertNotEquals(76866L, (long) test.getFirst());
        assertNotEquals("abc", test.getSecond());
    }

    @Test
    void customPairTestNull() {
        CustomPair<String, String> test = new CustomPair<>(null, null);
        assertNull(test.getFirst());
        assertNull(test.getSecond());
    }

    @Test
    void customPairTestToString() {
        CustomPair<Integer, Integer> test = new CustomPair<>(1, 2);
        assertEquals("CustomPair{first=1, second=2}", test.toString());
    }
}
