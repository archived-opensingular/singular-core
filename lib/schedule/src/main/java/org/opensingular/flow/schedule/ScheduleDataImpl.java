/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
