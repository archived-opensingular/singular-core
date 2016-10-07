/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.io;

import org.opensingular.form.RefService;

/**
 * Faz referência para um serviço que não deverá ser serializado, ou seja, o
 * valor será descartado em caso de serialização da referência. Tipicamente é
 * utilizado para referência do tipo cache ou que pode ser recalculada depois.
 *
 * @author Daniel C. Bordin
 */
public class ServiceRefTransientValue<T> implements RefService<T> {

    private final transient T value;

    public ServiceRefTransientValue(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

}
