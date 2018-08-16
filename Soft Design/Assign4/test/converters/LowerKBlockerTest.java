package converters;

import java.util.Map;

public class LowerKBlockerTest implements ConverterTest {
    public Converter createInstance() {
        return new LetterBlocker("k");
    }

    public Map<String, String> createTestSample() {
        return Map.of("a", "a", "abc", "abc", "akc", "ac", "akkb", "ab", "ak1k2", "a12");
    }
}
