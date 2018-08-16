package converters;

public class UpperCaseConverter implements Converter{
    public String convert(String text) {
        return text.toUpperCase();
    }
}