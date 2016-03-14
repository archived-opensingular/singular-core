/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.function;

import java.util.function.Function;

import br.net.mirante.singular.form.mform.SInstance;

@FunctionalInterface
public interface InstanciaFunction<I extends SInstance, R> extends Function<I, R> {

}
