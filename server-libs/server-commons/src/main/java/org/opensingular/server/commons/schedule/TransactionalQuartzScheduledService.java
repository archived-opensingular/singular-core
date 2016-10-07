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

package org.opensingular.server.commons.schedule;

import javax.inject.Inject;

import org.springframework.transaction.PlatformTransactionManager;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.flow.schedule.IScheduledJob;
import org.opensingular.flow.schedule.quartz.QuartzScheduleService;

public class TransactionalQuartzScheduledService extends QuartzScheduleService implements Loggable{

    @Inject
    private PlatformTransactionManager transactionManager;

    @Override
    public void schedule(IScheduledJob scheduledJob) {
        super.schedule(new TransactionalScheduledJobProxy(scheduledJob, transactionManager));
        
        getLogger().info("Job("+scheduledJob+") scheduled.");
    }
}
