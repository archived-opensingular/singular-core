/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.wicket.model;

import org.opensingular.singular.form.SInstance;
import org.opensingular.singular.form.event.SInstanceEvent;

import java.util.List;

public interface ISInstanceEventCollector<I extends SInstance> extends ISInstanceAwareModel<I> {
    List<SInstanceEvent> getInstanceEvents();
    void clearInstanceEvents();
}
