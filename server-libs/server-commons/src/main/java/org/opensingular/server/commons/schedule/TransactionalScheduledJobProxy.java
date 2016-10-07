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

import org.opensingular.flow.schedule.IScheduleData;
import org.opensingular.flow.schedule.IScheduledJob;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;


public class TransactionalScheduledJobProxy implements IScheduledJob {

    private final IScheduledJob              job;
    private final PlatformTransactionManager transactionManager;

    public TransactionalScheduledJobProxy(final IScheduledJob job, final PlatformTransactionManager transactionManager) {
        this.job = job;
        this.transactionManager = transactionManager;
    }

    @Override
    public IScheduleData getScheduleData() {
        return job.getScheduleData();
    }

    @Override
    public Object run() {
        return new TransactionTemplate(transactionManager).execute(status -> job.run());
    }

    @Override
    public String getId() {
        return job.getId();
    }

}