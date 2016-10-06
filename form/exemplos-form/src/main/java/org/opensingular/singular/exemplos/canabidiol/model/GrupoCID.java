/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.canabidiol.model;

import java.util.List;

public class GrupoCID extends AbstractDadoCID {

    private List<CategoriaCID> categorias;

    public List<CategoriaCID> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaCID> categorias) {
        this.categorias = categorias;
    }
}
