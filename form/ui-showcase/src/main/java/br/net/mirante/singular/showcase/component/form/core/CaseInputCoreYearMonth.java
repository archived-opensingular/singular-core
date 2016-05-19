/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInputCoreYearMonth extends CaseBase implements Serializable {

    public CaseInputCoreYearMonth() {
        super("Date", "Mês/Ano");
        setDescriptionHtml("Componente para inserção de mês e ano.");
    }
}
