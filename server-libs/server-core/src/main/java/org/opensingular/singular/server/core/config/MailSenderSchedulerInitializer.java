package org.opensingular.singular.server.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import org.opensingular.singular.flow.schedule.IScheduleService;
import org.opensingular.singular.flow.schedule.ScheduleDataBuilder;
import org.opensingular.singular.server.commons.config.SchedulerInitializer;
import org.opensingular.singular.server.core.service.DefaultMailSenderREST;
import org.opensingular.singular.server.core.service.EmailSender;
import org.opensingular.singular.server.core.service.EmailSenderScheduledJob;

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
