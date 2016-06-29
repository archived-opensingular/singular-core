/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.event.SInstanceEvent;

import java.util.List;

public interface IMInstanceEventCollector<I extends SInstance> extends IMInstanciaAwareModel<I> {
    List<SInstanceEvent> getInstanceEvents();
    void clearInstanceEvents();
}
