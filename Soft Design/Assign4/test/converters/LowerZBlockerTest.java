package converters;

import java.util.Map;

public class LowerZBlockerTest implements ConverterTest {

    public Converter createInstance() {
        return new LetterBlocker("z");
    }

    public Map<String, String> createTestSample() {
        return Map.of("a", "a", "abc", "abc", "azc", "ac", "azzb", "ab", "az1zB", "a1B");
    }
}
