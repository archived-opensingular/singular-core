package br.net.mirante.singular.server.p.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import br.net.mirante.singular.flow.schedule.IScheduleService;
import br.net.mirante.singular.flow.schedule.ScheduleDataBuilder;
import br.net.mirante.singular.server.commons.config.SchedulerInitializer;
import br.net.mirante.singular.server.commons.service.EmailSender;
import br.net.mirante.singular.server.commons.service.EmailSenderScheduledJob;

public class PSchedulerInitializer extends SchedulerInitializer {

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
}
