/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.server.commons.service;

import javax.inject.Inject;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.schedule.IScheduleData;
import br.net.mirante.singular.flow.schedule.IScheduledJob;
import br.net.mirante.singular.server.commons.service.dto.Email.Addressee;

public class EmailSenderScheduledJob implements IScheduledJob, Loggable {
    
    @Inject
    private EmailSender emailSender;
    
    @Inject
    private EmailPersistenceService emailPersistenceService;
    
    private IScheduleData scheduleData;
    
    private int emailsPerPage = 20;
    
    public EmailSenderScheduledJob(IScheduleData scheduleData) {
        super();
        this.scheduleData = scheduleData;
    }

    @Override
    public IScheduleData getScheduleData() {
        return scheduleData;
    }

    @Override
    public Object run() {
        final int totalPendingRecipients = emailPersistenceService.countPendingRecipients();
        int pending = totalPendingRecipients;
        int page = 0, sent = 0;
        while (pending > 0) {
            for (Addressee addressee : emailPersistenceService.listPendingRecipients(page * emailsPerPage, emailsPerPage)) {
                if(emailSender.send(addressee)){
                    emailPersistenceService.markAsSent(addressee);
                    sent++;
                }
            }
            pending -= emailsPerPage;
            page++;
        }
        getLogger().info(sent + " sent from total of "+totalPendingRecipients);
        return sent + " sent from total of "+totalPendingRecipients;
    }

    public void setEmailsPerPage(int emailsPerPage) {
        this.emailsPerPage = emailsPerPage;
    }
        
    @Override
    public String getId() {
        return "EmailSender";
    }

    @Override
    public String toString() {
        return "EmailSenderScheduledJob [getScheduleData()=" + getScheduleData() + ", getId()=" + getId() + "]";
    }

}
