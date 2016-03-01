package br.net.mirante.singular.util;


import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T, X> X getFromArray(T[] array, Function<Stream<T>, X> statement) {
        return autoCloseAndGet(Arrays.stream(array), statement);
    }

    public static <T> void fromArray(T[] array, Consumer<Stream<T>> statement) {
        autoClose(Arrays.stream(array), statement);
    }

    public static <T> void autoClose(Stream<T> stream, Consumer<Stream<T>> statement) {
        statement.accept(stream);
        stream.close();
    }

    public static <T, X> X autoCloseAndGet(Stream<T> stream, Function<Stream<T>, X> statement) {
        final X x = statement.apply(stream);
        stream.close();
        return x;
    }
}
