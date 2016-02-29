package br.net.mirante.singular.util;


import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T, X> X fromArray(T[] array, Function<Stream<T>, X> statement) {
        return autoClose(Arrays.stream(array), statement);
    }

    public static <T, X> X autoClose(Stream<T> stream, Function<Stream<T>, X> statement) {
        final X x = statement.apply(stream);
        stream.close();
        return x;
    }
}
