package br.net.mirante.singular.util.wicket.lambda;

import java.io.Serializable;
import java.util.function.Function;

public interface IFunction<T, R> extends Function<T, R>, Serializable {

}
