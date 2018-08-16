package converters;

import java.util.Map;

public class MultiplierTest implements ConverterTest {

    public Converter createInstance() {
        return new Multiplier();
    }

    public Map<String, String> createTestSample() {
        return Map.of("a", "aa", "1", "11", "aa", "aaaa", "abc", "aabbcc");
    }
}
