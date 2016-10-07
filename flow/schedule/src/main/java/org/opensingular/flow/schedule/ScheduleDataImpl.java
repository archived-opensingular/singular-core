/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.flow.schedule;

class ScheduleDataImpl implements IScheduleData {

    private final String cronExpression;

    private final String description;

    public ScheduleDataImpl(String cronExpression, String description) {
        this.cronExpression = cronExpression;
        this.description = description;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ScheduleDataImpl [cronExpression=" + cronExpression + ", description=" + description + "]";
    }
}
