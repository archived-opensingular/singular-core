package org.opensingular.lib.commons.lambda;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

//TODO Verificar possibilidade de inclusão da dependencia do StreamEX
public final class StreamUtil {

    private StreamUtil() {
        throw new UnsupportedOperationException("No " + StreamUtil.class.getSimpleName() + " instances for you!");
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
