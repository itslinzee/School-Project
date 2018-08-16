package game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpellCheckerServiceTest {

    private SpellCheckerService service;

    @BeforeEach
    void init() {
        service = new SpellCheckerService();
    }

    @Test
    void canary() {
        assertTrue(true);
    }

    @Test
    void testCorrectSpelling() {
        assertEquals(true, service.isSpellingCorrect("monkey"));
    }

    @Test
    void testIncorrectSpelling() {
        assertEquals(false, service.isSpellingCorrect("nkeymo"));
    }

    @Test
    void testForNetworkFailure() {
        service.setServiceURL("Bad URL");

        assertThrows(RuntimeException.class, () -> service.isSpellingCorrect("monkey"), "Network error");
    }
}
