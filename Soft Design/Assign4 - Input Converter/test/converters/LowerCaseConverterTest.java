package converters;

import java.util.Map;

public class LowerCaseConverterTest implements ConverterTest{
    public Converter createInstance() {
        return new LowerCaseConverter();
    }

    public Map<String, String> createTestSample() {
        return Map.of("A", "a", "AA", "aa", "ABCDEFGHI", "abcdefghi");
    }

}