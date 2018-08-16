package processors;

import converters.Converter;

import java.util.List;

public class Processor {

  private Converter converter;

  public Processor(List<Converter> converters) {
    converter = converters.stream()
            .reduce(input -> input, Converter::andThen);
  }

  public String process(String text) {
    return converter.convert(text);
  }
}
