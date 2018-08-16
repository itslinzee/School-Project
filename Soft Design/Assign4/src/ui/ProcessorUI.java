package ui;

import converters.*;
import processors.Processor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;

public class ProcessorUI {
    public static void main(String [] args) {
        try {
            String blocks = readFile("/inputs/blockers.txt");
            List<Converter> converters = ConverterFactory.createConverters(
                    Arrays.asList(blocks.split("\n")));

            String text = readFile("/inputs/input.txt");
            useConverters(converters, text);
        }
        catch(Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static String readFile(String fileName) throws IOException {
        fileName = System.getProperty("user.dir").concat(fileName);

        return Files.lines(Paths.get(fileName))
                .collect(joining("\n"));
    }

    private static void useConverters(List<Converter> converters, String inputText) {
        Processor processor = new Processor(converters);

        System.out.println(processor.process(inputText));
    }
}
