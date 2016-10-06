/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.flow.schedule;

public interface IScheduleService {

    void schedule(IScheduledJob scheduledJob);

    void trigger(IScheduledJob scheduledJob);
}
