package processors;

import converters.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcessorTest {
    @Test
    void testZblockerAndUpper() {
        Processor processor = new Processor(List.of(new LetterBlocker("Z"), new UpperCaseConverter()));

        assertEquals("AA", processor.process("aZa"));
    }

    @Test
    void testZblockerAndLower() {
        Processor processor = new Processor(List.of(new LetterBlocker("Z"), new LowerCaseConverter()));

        assertEquals("aa", processor.process("AZA"));
    }

    @Test
    void testUpperZblockerAndLower() {
        Processor processor = new Processor(List.of(new UpperCaseConverter(), new LetterBlocker("Z"), new LowerCaseConverter()));

        assertEquals("aa", processor.process("aza"));
    }

    @Test
    void testUpperZblockerkblockerAndLower() {
        Processor processor = new Processor(List.of(new UpperCaseConverter(), new LetterBlocker("Z"),
                new LetterBlocker("k"), new LowerCaseConverter()));

        assertEquals("aka", processor.process("azka"));
    }

    @Test
    void testUpperzblockerkblockerAndLower() {
        Processor processor = new Processor(List.of(new UpperCaseConverter(), new LetterBlocker("z"),
                new LetterBlocker("k"), new LowerCaseConverter()));

        assertEquals("azka", processor.process("azka"));
    }
}
