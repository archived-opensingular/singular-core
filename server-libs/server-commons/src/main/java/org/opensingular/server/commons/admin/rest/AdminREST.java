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

package org.opensingular.server.commons.admin.rest;

import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.flow.schedule.ScheduledJob;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.sf.ehcache.CacheManager;

@RestController
@RequestMapping("/admin")
public class AdminREST {

    @Autowired
    private IScheduleService scheduleService;

    @RequestMapping(value = "/run-all-jobs", method = RequestMethod.GET)
    public void runAllJobs() throws SchedulerException {
        for (JobKey jobKey : scheduleService.getAllJobKeys()) {
            ScheduledJob scheduledJob = new ScheduledJob(jobKey.getName(), null, null);
            scheduleService.trigger(scheduledJob);
        }

    }

    @RequestMapping(value = "/clear-caches", method = RequestMethod.GET)
    public void clearCaches() {
        CacheManager.getInstance().clearAll();
    }

}
