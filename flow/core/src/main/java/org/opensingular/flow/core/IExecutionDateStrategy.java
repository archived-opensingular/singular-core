/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.core;

import java.util.Date;
import java.util.function.BiFunction;

@FunctionalInterface
public interface IExecutionDateStrategy<K extends ProcessInstance> extends BiFunction<K, TaskInstance, Date> {

}