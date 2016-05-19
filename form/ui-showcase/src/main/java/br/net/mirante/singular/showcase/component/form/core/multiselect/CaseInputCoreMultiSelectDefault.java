/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.multiselect;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInputCoreMultiSelectDefault extends CaseBase implements Serializable  {

    public CaseInputCoreMultiSelectDefault() {
        super("Multi Select", "Default");
        setDescriptionHtml("Se a view não for definida, então define o componente dependendo da quantidade de dados.");
    }

}
