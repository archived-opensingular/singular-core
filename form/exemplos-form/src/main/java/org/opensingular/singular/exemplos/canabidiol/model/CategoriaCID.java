/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.exemplos.canabidiol.model;

import java.util.List;

public class CategoriaCID extends AbstractDadoCID {

    private List<SubCategoriaCID> subCategorias;

    public List<SubCategoriaCID> getSubCategorias() {
        return subCategorias;
    }

    public void setSubCategorias(List<SubCategoriaCID> subCategorias) {
        this.subCategorias = subCategorias;
    }
}
