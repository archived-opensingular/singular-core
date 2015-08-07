package br.net.mirante.singular.flow.core;

import java.util.Date;
import java.util.function.BiFunction;

@FunctionalInterface
public interface IExecutionDateStrategy<K extends ProcessInstance> extends BiFunction<K, TaskInstance, Date> {

}