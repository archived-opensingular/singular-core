/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core.select;


import java.io.Serializable;

import br.net.mirante.singular.studio.component.CaseBase;
import br.net.mirante.singular.studio.component.ResourceRef;

public class CaseInputCoreSelectCompositePojo extends CaseBase implements Serializable {

    public CaseInputCoreSelectCompositePojo() {
        super("Select", "Tipo composto com objetos serializaveis.");
        setDescriptionHtml("É possivel utilizar objetos serializaveis para realizar a seleção, porem neste caso, é necessario informar o conversor.");
        getAditionalSources().add(ResourceRef.forSource(IngredienteQuimico.class).orElse(null));
        getAditionalSources().add(ResourceRef.forSource(IngredienteQuimicoFilteredProvider.class).orElse(null));
    }

}
