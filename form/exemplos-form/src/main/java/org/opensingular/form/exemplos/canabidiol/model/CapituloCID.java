/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.exemplos.canabidiol.model;

import java.util.List;

public class CapituloCID extends AbstractDadoCID {

    private Integer capitulo;

    private List<GrupoCID> grupos;

    public Integer getCapitulo() {
        return capitulo;
    }

    public void setCapitulo(Integer capitulo) {
        this.capitulo = capitulo;
    }

    public List<GrupoCID> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoCID> grupos) {
        this.grupos = grupos;
    }
}
