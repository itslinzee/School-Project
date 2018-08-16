package converters;

import java.util.Map;

public class UpperZBlockerTest implements ConverterTest {
    public Converter createInstance() {
        return new LetterBlocker("Z");
    }

    public Map<String, String> createTestSample() {
        return Map.of("a", "a", "abc", "abc", "aZc", "ac", "aZZb", "ab", "aZ1Z2", "a12", "azaZ", "aza");
    }
}
