package converters;

public class LowerCaseConverter implements Converter{
    public String convert(String text) {
        return text.toLowerCase();
    }
}