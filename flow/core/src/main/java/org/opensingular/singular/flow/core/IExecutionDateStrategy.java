/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.core;

import java.util.Date;
import java.util.function.BiFunction;

@FunctionalInterface
public interface IExecutionDateStrategy<K extends ProcessInstance> extends BiFunction<K, TaskInstance, Date> {

}