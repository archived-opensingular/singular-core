/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core;

import br.net.mirante.singular.showcase.component.CaseBase;
import java.io.Serializable;

public class CaseInputCoreInteger extends CaseBase implements Serializable {

    public CaseInputCoreInteger() {
        super("Numeric", "Integer");
        setDescriptionHtml("Campo para edição de dados inteiro");
    }

}
