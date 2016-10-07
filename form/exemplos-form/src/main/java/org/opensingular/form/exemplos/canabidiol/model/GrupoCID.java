/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.canabidiol.model;

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
