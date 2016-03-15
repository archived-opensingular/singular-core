/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.model;

import java.util.List;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.event.SInstanceEvent;

public interface IMInstanceEventCollector<I extends SInstance> extends IMInstanciaAwareModel<I> {
    List<SInstanceEvent> getInstanceEvents();
    void clearInstanceEvents();
}
