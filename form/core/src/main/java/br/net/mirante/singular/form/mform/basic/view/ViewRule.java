/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.mform.basic.view;

import java.util.function.Function;

import com.google.common.base.Throwables;

import br.net.mirante.singular.form.mform.SInstance;

/**
 * Representa uma regra de mapeamento de uma instância em uma view. Se a regra
 * não se aplica é esperado que a mesma retorna uma view null.
 *
 * @author Daniel C. Bordin
 */
public abstract class ViewRule implements Function<SInstance, SView> {

    /**
     * Retorna uma view se a regra se aplicar ao caso ou null senão se aplica.
     */
    @Override
    public abstract SView apply(SInstance instance);

    /** Método de apoio. Cria uma instância a partir da classe. */
    protected final static SView newInstance(Class<? extends SView> view) {
        try {
            return view.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }
}
