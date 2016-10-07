/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.canabidiol.model;

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
