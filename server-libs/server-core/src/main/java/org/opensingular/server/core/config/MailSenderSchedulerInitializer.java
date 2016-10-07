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

package org.opensingular.server.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.flow.schedule.ScheduleDataBuilder;
import org.opensingular.server.commons.config.SchedulerInitializer;
import org.opensingular.server.core.service.DefaultMailSenderREST;
import org.opensingular.server.core.service.EmailSender;
import org.opensingular.server.core.service.EmailSenderScheduledJob;

public class MailSenderSchedulerInitializer extends SchedulerInitializer {

    @Bean
    public EmailSender emailSender(){
        return new EmailSender();
    }
    
    @Bean
    @DependsOn({"emailSender", "scheduleService", "emailService"})
    public EmailSenderScheduledJob scheduleEmailSenderJob(IScheduleService scheduleService){
        EmailSenderScheduledJob emailSenderScheduledJob = new EmailSenderScheduledJob(ScheduleDataBuilder.buildMinutely(5));
        scheduleService.schedule(emailSenderScheduledJob);
        return emailSenderScheduledJob;
    }
    
    @Bean
    public DefaultMailSenderREST mailSenderREST(){
        return new DefaultMailSenderREST();
    }
}
