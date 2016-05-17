/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core;

import java.io.Serializable;

import br.net.mirante.singular.studio.component.CaseBase;

public class CaseInputCoreDate extends CaseBase implements Serializable {

    public CaseInputCoreDate() {
        super("Date", "Simples");
        setDescriptionHtml("Componente para inserção de data");
    }
}
