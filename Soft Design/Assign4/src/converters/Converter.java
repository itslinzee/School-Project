package converters;

public interface Converter {
    String convert(String text);
    
    default Converter andThen(Converter nextConverter) {
      return text -> nextConverter.convert(convert(text));
    }
}
