/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.function;

import org.opensingular.form.SInstance;

import java.util.function.Function;

@FunctionalInterface
public interface InstanciaFunction<I extends SInstance, R> extends Function<I, R> {

}
