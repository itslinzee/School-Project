package converters;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public interface ConverterFactory {

    static List<Converter> createConverters(List<String> convertersDetails) {

        return convertersDetails.stream()
                .map(ConverterFactory::createConverter)
                .collect(toList());
    }

    static Converter createConverter(String converterDetails) {

        String[] details = converterDetails.split(" ");

        try {
            Class<?> classType = Class.forName(details[0]);

            Class<?>[] parameterTypes = Arrays.asList(classType.getDeclaredConstructors())
                    .get(0).getParameterTypes();

            return (Converter) classType.getDeclaredConstructor(parameterTypes)
                    .newInstance(Arrays.copyOfRange(details, 1, details.length));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

