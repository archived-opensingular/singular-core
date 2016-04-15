/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.interaction;

import br.net.mirante.singular.showcase.component.CaseBase;

public class CaseInitListener extends CaseBase {

    public CaseInitListener() {
        super("Listeners", "Init Listener");
        setDescriptionHtml("Listener que é executado ao criar uma nova instância de um tipo");
    }
}
