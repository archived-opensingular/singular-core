package br.net.mirante.singular.util.wicket.lambda;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface IBiFunction<T, U, R> extends BiFunction<T, U, R>, Serializable {

}
