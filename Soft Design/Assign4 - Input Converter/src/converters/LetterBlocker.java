package converters;

public class LetterBlocker implements Converter {

    private final String letterToBlock;

    public LetterBlocker(String letter) {
        letterToBlock = letter;
    }

    public String convert(String text) {
        return text.replaceAll(letterToBlock, "");
    }
}
