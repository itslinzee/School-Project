package converters;

public class Multiplier implements Converter{
    public String convert(String text) {
        return text.replaceAll(".", "$0$0");
    }
}
