/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.multiselect;

import java.io.Serializable;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInputCoreMultiSelectProvider extends CaseBase implements Serializable {

    public CaseInputCoreMultiSelectProvider() {
        super("Multi Select", "Provedor Dinâmico");
        setDescriptionHtml("É permitido alterar o provedor de dados de forma que estes sejam carregados de forma dinâmica ou de outras fontes de informação.");
    }
}