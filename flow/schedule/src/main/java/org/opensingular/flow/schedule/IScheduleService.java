/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.schedule;

public interface IScheduleService {

    void schedule(IScheduledJob scheduledJob);

    void trigger(IScheduledJob scheduledJob);
}
