package converters;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterFactoryTest {
    @Test
    void testCreateConverters() {
        List<String> input = Arrays.asList("converters.UpperCaseConverter", "converters.LetterBlocker K");

        List<Converter> converters = ConverterFactory.createConverters(input);

        assertAll(
                () -> assertEquals(2, converters.size()),
                () -> assertTrue(input.contains(converters.get(0).getClass().getName())),
                () -> assertTrue(converters.get(0) instanceof Converter));
    }

    @Test
    void testCreateConvertersThrowsException() {
        List<String> converters = Arrays.asList("Non-existent converter");

        assertThrows(RuntimeException.class, () -> ConverterFactory.createConverters(converters));
    }
}
