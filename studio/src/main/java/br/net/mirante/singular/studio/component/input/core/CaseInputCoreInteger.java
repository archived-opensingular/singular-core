/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core;

import java.io.Serializable;

import br.net.mirante.singular.studio.component.CaseBase;

public class CaseInputCoreInteger extends CaseBase implements Serializable {

    public CaseInputCoreInteger() {
        super("Numeric", "Integer");
        setDescriptionHtml("Campo para edição de dados inteiro");
    }

}
