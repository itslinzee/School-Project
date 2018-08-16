package converters;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class UpperCaseConverterTest implements ConverterTest{
    @Test
    void canary() {
        assertTrue(true);
    }

    public Converter createInstance() {
        return new UpperCaseConverter();
    }

    public Map<String, String> createTestSample() {
        return Map.of("a", "A", "aa", "AA", "abcdefghi", "ABCDEFGHI");
    }
}