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
package org.opensingular.server.core.service;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.opensingular.flow.schedule.IScheduleService;
import org.opensingular.server.commons.service.IMailSenderREST;
import org.opensingular.lib.support.spring.util.AutoScanDisabled;

@AutoScanDisabled
@RequestMapping(IMailSenderREST.PATH)
@RestController
public class DefaultMailSenderREST implements IMailSenderREST{

    @Inject
    private IScheduleService scheduleService;
    
    @Inject
    private EmailSenderScheduledJob emailSenderScheduledJob;
    
    @RequestMapping(value = PATH_SEND_ALL, method = RequestMethod.GET)
    public boolean sendAll() {
        try {
            scheduleService.trigger(emailSenderScheduledJob);
            return true;
        } catch (Exception e) {
            getLogger().error("Erro ao disparar envio de email", e);
            return false;
        }
    }

}
