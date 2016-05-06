/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.event;

import br.net.mirante.singular.form.SInstance;

public class SInstanceStructureChangeEvent extends SInstanceEvent {

    public SInstanceStructureChangeEvent(SInstance source) {
        super(source);
    }
}
