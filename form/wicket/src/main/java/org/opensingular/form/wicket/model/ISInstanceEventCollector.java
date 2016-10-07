/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.wicket.model;

import org.opensingular.form.SInstance;
import org.opensingular.form.event.SInstanceEvent;

import java.util.List;

public interface ISInstanceEventCollector<I extends SInstance> extends ISInstanceAwareModel<I> {
    List<SInstanceEvent> getInstanceEvents();
    void clearInstanceEvents();
}
