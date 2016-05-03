/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.showcase.component.CaseBase;

import java.io.Serializable;

public class CaseInputCoreSelectComposite extends CaseBase implements Serializable {

    public CaseInputCoreSelectComposite() {
        super("Select", "Tipo Composto");
        setDescriptionHtml("Pemite a seleção de valores compostos de varios tipos diferentes.");
    }

}
